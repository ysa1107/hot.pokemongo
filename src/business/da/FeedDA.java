/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business.da;

import com.kyt.framework.config.LogUtil;
import com.kyt.framework.dbconn.ClientManager;
import com.kyt.framework.dbconn.ManagerIF;
import com.kyt.framework.util.ConvertUtils;
import com.mysql.jdbc.Statement;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;
import entity.FeedEnt;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Y Sa
 */
public class FeedDA {
    private static final org.apache.log4j.Logger _logger = LogUtil.getLogger(FeedDA.class);
    
    private static final Lock createLock_ = new ReentrantLock();
    private static final Map<String, FeedDA> sInstance = new HashMap<>();
    private String configName = "database";
    
    public static FeedDA getInstance(String configName) {
        if (sInstance.get(configName) == null) {
            try {
                createLock_.lock();
                sInstance.put(configName, new FeedDA(configName));
            } finally {
                createLock_.unlock();
            }
        }
        return sInstance.get(configName);
    }
    
    public FeedDA(String configName) {
        this.configName = configName;
    }
    
    private static final String QUERY_FIELD
            = "f.`id`, "
            + "f.`title`, "
            + "f.`description`, "
            + "f.`thumbnail`, "
            + "f.`media_type`, "
            + "f.`media_url`, "
            + "f.`share_url`, "
            + "f.`liked`, "
            + "f.`viewed`, "
            + "f.`commented`, "
            + "f.`downloaded`, "
            + "f.`created`, "
            + "f.`state`, "
            + "f.`source`, "
            + "f.`ext_key`, "
            + "f.`md5`, "
            + "f.`duration`, "
            + "f.`score`, "
            + "f.`tags`, "
            + "f.`alternate_url`, "
            + "f.`width`, "
            + "f.`height`, "
            + "f.`readmore` ";
    private static final String INSERT_QUERY = "INSERT INTO feed ( "
            + "`title`, "
            + "`description`, "
            + "`thumbnail`, "
            + "`media_type`, "
            + "`media_url`, "
            + "`share_url`, "
            + "`liked`, "
            + "`viewed`, "
            + "`commented`, "
            + "`downloaded`, "
            + "`created`, "
            + "`state`, "
            + "`source`, "
            + "`ext_key`, "
            + "`md5`, "
            + "`duration`, "
            + "`score`, "
            + "`tags`, "
            + "`readmore`, "
            + "`alternate_url`, "
            + "`category_id`, "
            + "`order_time`, "
            + "`width`, "
            + "`height` "
            + " ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    
    private static final String INCREASE_QUERY = "UPDATE feed SET `%s`=`%s`+1 WHERE id=?";
    
    private static final String GET_BY_HOT_QUERY = "SELECT " + QUERY_FIELD + " FROM feed f WHERE f.`state`=1 "
            //+ " AND f.`category_id`=? "
            + " ORDER BY `liked` DESC LIMIT ?,?";
    private static final String GET_BY_NEW_QUERY = "SELECT " + QUERY_FIELD + " FROM feed f WHERE f.`state`=1 "
            //+ " AND f.`category_id`=? "
            + " ORDER BY `order_time` DESC LIMIT ?,?";
    private static final String GET_BY_HOT_WITH_CATE_QUERY = "SELECT " + QUERY_FIELD + " FROM feed f WHERE f.`state`=1 "
            + " AND f.`category_id`=? "
            + " ORDER BY `liked` DESC LIMIT ?,?";
    private static final String GET_BY_NEW_WITH_CATE_QUERY = "SELECT " + QUERY_FIELD + " FROM feed f WHERE f.`state`=1 "
            + " AND f.`category_id`=? "
            + " ORDER BY `order_time` DESC LIMIT ?,?";
    private static final String EXIST_QUERY = "SELECT " + QUERY_FIELD + "  FROM feed f WHERE md5=?";
    private static final String GET_QUERY = "SELECT " + QUERY_FIELD + "  FROM feed f WHERE state=1 AND id=?";
    private static final String GET_PAGING_BY_GENRE = "SELECT " + QUERY_FIELD + " FROM feed f, genres_feed g WHERE g.`feed_id`=f.`id` AND f.state=1 AND g.`genres_id`=? ORDER BY f.`order_time` DESC  LIMIT ?,?";
    private static final String GET_BY_TAG = "SELECT " + QUERY_FIELD + " FROM feed f WHERE f.`state`=1 AND category_id=? AND MATCH(f.`tags`) AGAINST (?) ORDER BY f.`order_time` DESC LIMIT ?,?";
    
    public List<FeedEnt> getPagingByGenre(int genreId, int start, int offset) {
        List<FeedEnt> result = new ArrayList();
        ManagerIF cm = null;
        Connection con = null;
        try {
            cm = ClientManager.getInstance(this.configName);
            con = cm.borrowClient();
            try (PreparedStatement stmt = con.prepareStatement(GET_PAGING_BY_GENRE)) {
                stmt.setInt(1, genreId);
                stmt.setInt(2, start * offset);
                stmt.setInt(3, offset);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    result.add(buildShortEnt(rs));
                }
            }
            return result;
        } catch (SQLException e) {
            _logger.error(e.getStackTrace());
        } finally {
            if ((cm != null) && (con != null)) {
                cm.returnClient(con);
            }
        }
        return null;
    }
    
    public List<FeedEnt> getPagingByTag(int category_id, String tag, int start, int offset) {
        List<FeedEnt> result = new ArrayList();
        ManagerIF cm = null;
        Connection con = null;
        try {
            cm = ClientManager.getInstance(this.configName);
            con = cm.borrowClient();
            try (PreparedStatement stmt = con.prepareStatement(GET_BY_TAG)) {
                stmt.setInt(1, category_id);
                stmt.setString(2, tag);
                stmt.setInt(3, start * offset);
                stmt.setInt(4, offset);
                
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    result.add(buildShortEnt(rs));
                }
            }
            return result;
        } catch (SQLException e) {
            _logger.error(e.getStackTrace());
        } finally {
            if ((cm != null) && (con != null)) {
                cm.returnClient(con);
            }
        }
        return null;
    }
    
    public List<FeedEnt> getPaging(int category_id, int start, int offset, String type) {
        List<FeedEnt> result = new ArrayList();
        ManagerIF cm = null;
        Connection con = null;
        try {
            cm = ClientManager.getInstance(this.configName);
            con = cm.borrowClient();
            if (category_id == 1) {
                String query = type.equals("byhot") ? GET_BY_NEW_QUERY : GET_BY_HOT_QUERY;
                try (PreparedStatement stmt = con.prepareStatement(query)) {
//                    stmt.setInt(1, category_id);
                    stmt.setInt(1, start * offset);
                    stmt.setInt(2, offset);
                    
                    System.out.println(stmt.toString());
                    
                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        result.add(buildShortEnt(rs));
                    }
                }
                return result;
            } else {
                String query = type.equals("byhot") ? GET_BY_NEW_WITH_CATE_QUERY : GET_BY_HOT_WITH_CATE_QUERY;
                try (PreparedStatement stmt = con.prepareStatement(query)) {
                    stmt.setInt(1, category_id);
                    stmt.setInt(2, start * offset);
                    stmt.setInt(3, offset);
                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        result.add(buildShortEnt(rs));
                    }
                }
                return result;
            }
        } catch (SQLException e) {
            _logger.error(e.getStackTrace());
        } finally {
            if ((cm != null) && (con != null)) {
                cm.returnClient(con);
            }
        }
        return result;
    }
    
    public FeedEnt get(int id) {
        ManagerIF cm = null;
        Connection con = null;
        try {
            cm = ClientManager.getInstance(this.configName);
            con = cm.borrowClient();
            try (PreparedStatement stmt = con.prepareStatement(GET_QUERY, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, id);
                _logger.info("database: " + configName + ":: " + stmt.toString());                
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return buildShortEnt(rs);
                }
            }
        } catch (SQLException e) {
            _logger.error(e.getSQLState());
        } finally {
            
            if (cm != null && con != null) {
                cm.returnClient(con);
            }
        }
        return null;
    }
    
    public FeedEnt exist(String md5) {
        ManagerIF cm = null;
        Connection con = null;
        try {
            cm = ClientManager.getInstance(this.configName);
            con = cm.borrowClient();
            try (PreparedStatement stmt = con.prepareStatement(EXIST_QUERY, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, md5);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return buildEnt(rs);
                }
                
            }
        } catch (SQLException e) {
            _logger.error(e.getSQLState());
            
        } finally {
            
            if (cm != null && con != null) {
                cm.returnClient(con);
            }
        }
        return null;
    }
    
    public boolean increase(int id, String field) {
        ManagerIF cm = null;
        Connection con = null;
        try {
            cm = ClientManager.getInstance(this.configName);
            con = cm.borrowClient();
            String query = String.format(INCREASE_QUERY, field, field);
            try (PreparedStatement stmt = con.prepareStatement(query, Statement.NO_GENERATED_KEYS)) {
                stmt.setInt(1, id);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            _logger.error(e.getSQLState());
        } finally {
            
            if (cm != null && con != null) {
                cm.returnClient(con);
            }
        }
        return false;
    }
    
    public boolean updateByQuery(String query) {
        ManagerIF cm = null;
        Connection con = null;
        try {
            cm = ClientManager.getInstance(this.configName);
            con = cm.borrowClient();
            try (PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            _logger.error(e.getSQLState());
        } finally {
            
            if (cm != null && con != null) {
                cm.returnClient(con);
            }
        }
        return false;
    }
    
    public boolean insert(FeedEnt value) {
        boolean result = false;
        ManagerIF cm = null;
        Connection con = null;
        try {
            cm = ClientManager.getInstance(this.configName);
            con = cm.borrowClient();
            try (PreparedStatement stmt = con.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, value.title);
                stmt.setString(2, value.description);
                stmt.setString(3, value.thumbnail);
                stmt.setInt(4, value.media_type);
                stmt.setString(5, value.media_url);
                stmt.setString(6, value.share_url);
                stmt.setInt(7, value.liked);
                stmt.setInt(8, value.viewed);
                stmt.setInt(9, value.commented);
                stmt.setInt(10, value.downloaded);
                stmt.setTimestamp(11, new Timestamp(value.created));
                stmt.setInt(12, value.state);
                stmt.setString(13, value.source);
                stmt.setString(14, value.ext_key);
                stmt.setString(15, value.md5);
                stmt.setString(16, value.duration);
                stmt.setInt(17, value.score);
                stmt.setString(18, value.tags.toLowerCase());
                stmt.setBoolean(19, value.readmore);
                stmt.setString(20, value.alternate_url);
                stmt.setInt(21, value.category_id);
                stmt.setLong(22, System.currentTimeMillis());
                stmt.setInt(23, value.width);
                stmt.setInt(24, value.height);
                if (stmt.executeUpdate() > 0) {
                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        rs.next();
                        value.id = rs.getInt(1);
                    }
                    result = true;
                }
            }
        } catch (MySQLIntegrityConstraintViolationException ex) {
            _logger.info("Existed: " + value.title);
        } catch (SQLException e) {
            _logger.error(e.getSQLState());
            e.printStackTrace();
            
        } finally {
            
            if (cm != null && con != null) {
                cm.returnClient(con);
            }
        }
        return result;
    }
    
    public FeedEnt buildEnt(ResultSet rs) throws SQLException {
        FeedEnt ent = new FeedEnt();
        ent.id = rs.getInt("id");
        ent.title = ConvertUtils.toString(rs.getString("title"), "");
        ent.description = ConvertUtils.toString(rs.getString("description"), "");
        ent.thumbnail = rs.getString("thumbnail");
        ent.media_type = rs.getInt("media_type");
        ent.media_url = rs.getString("media_url");
        ent.share_url = ConvertUtils.toString(rs.getString("share_url"), "");
        ent.liked = rs.getInt("liked");
        ent.viewed = rs.getInt("viewed");
        ent.downloaded = rs.getInt("downloaded");
        ent.commented = rs.getInt("commented");
        ent.created = rs.getTimestamp("created").getTime();
        ent.state = rs.getInt("state");
        ent.ext_key = rs.getString("ext_key");
        ent.md5 = rs.getString("md5");
        ent.duration = ConvertUtils.toString(rs.getString("duration"), "");
        ent.tags = ConvertUtils.toString(rs.getString("tags"), "");
        ent.score = rs.getInt("score");
        ent.readmore = rs.getBoolean("readmore");
        ent.alternate_url = ConvertUtils.toString(rs.getString("alternate_url"), "");
        return ent;
    }
    
    public FeedEnt buildShortEnt(ResultSet rs) throws SQLException {
        FeedEnt ent = new FeedEnt();
        ent.id = rs.getInt("id");
        ent.title = ConvertUtils.toString(rs.getString("title"), "");
        ent.description = ConvertUtils.toString(rs.getString("description"), "");
        ent.thumbnail = rs.getString("thumbnail");
        ent.media_type = rs.getInt("media_type");
        ent.media_url = rs.getString("media_url");
//        ent.share_url = ConvertUtil.toString(rs.getString("share_url"), "");
        ent.liked = rs.getInt("liked");
        ent.viewed = rs.getInt("viewed");
        ent.downloaded = rs.getInt("downloaded");
        ent.commented = rs.getInt("commented");
        ent.created = rs.getTimestamp("created").getTime();
        ent.state = rs.getInt("state");
        ent.duration = ConvertUtils.toString(rs.getString("duration"), "");
        ent.tags = ConvertUtils.toString(rs.getString("tags"), "");
        ent.alternate_url = ConvertUtils.toString(rs.getString("alternate_url"), "");
        ent.score = rs.getInt("score");
        ent.readmore = rs.getBoolean("readmore");
        ent.width = rs.getInt("width");
        ent.height = rs.getInt("height");
        ent.share_url = "http://gagsmile.com/detail/" + ent.id;       
        return ent;
    }
}
