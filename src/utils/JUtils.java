package utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.RandomAccess;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.WordUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

/**
 *
 * @author Y Sa
 */
public class JUtils {

    public static class Jsons {

        private static final Gson json = new GsonBuilder()
                .disableHtmlEscaping()
                .create();
        private static final JsonParser jsonParser = new JsonParser();

        public static String Serialize(Object value) {
            return json.toJson(value);
        }

        public static <T> T DeSerialize(String value, Type typeOfT) {
            T result = json.fromJson(value, typeOfT);
            return result;
        }

        public static JsonElement parse(String data) {
            return jsonParser.parse(data);
        }
    }

    public static class Maps {

        public static <V> SortedMap<String, V> subMap(Map<String, V> map, String regex) {
            if (regex.endsWith(".")) {
                regex = regex.substring(0, regex.length() - 1);
            }
            SortedMap sorted = new TreeMap();
            sorted.putAll(map);
            return sorted.subMap(regex + ".", regex + "/");
        }

        /**
         * Order the map by map value
         *
         * @param <K>
         * @param <V> extends Number
         * @param map the map will be ordered
         * @param isDESC order by ASC|DESC
         * @return the map has been ordered
         *
         * Usage Example: <br />      <code>
         * HashMap<String, Double> map = new HashMap<String, Double>(); <br />
         * map.put("A", 99.5); <br />
         * map.put("E", 11.1); <br />
         * map.put("F", 111.1); <br />
         * map.put("B", 67.4); <br />
         * map.put("C", 67.4); <br />
         * map.put("D", 67.3); <br />
         * TreeMap<String, Double> sorted_map = sortByValue(map, true); <br />
         * sorted_map.putAll(map);
         * </code>
         */
        public static <K, V> TreeMap<K, V> sortByValue(Map<K, V> map, boolean isDESC) {
            if (isDESC) {
                ValueComparatorDESC bvc = new ValueComparatorDESC(map);
                return new TreeMap<K, V>(bvc);
            } else {
                ValueComparatorASC bvc = new ValueComparatorASC(map);
                return new TreeMap<K, V>(bvc);
            }
        }

        public static <K, V> TreeMap<K, V> sortByValue(Map<K, V> map, boolean isDESC, final String compareField, final Class<V> clazz) {
            try {
                if (isDESC) {
                    ValueCompareObjectFieldDESC bvc = new ValueCompareObjectFieldDESC(map, compareField, clazz);
                    return new TreeMap<K, V>(bvc);
                } else {
                    ValueCompareObjectFieldASC bvc = new ValueCompareObjectFieldASC(map, compareField, clazz);
                    return new TreeMap<K, V>(bvc);
                }
            } catch (Exception e) {
                return new TreeMap<K, V>();
            }
        }

        /**
         * Order the map by map key
         *
         * @param <K>
         * @param <V>
         * @param map the map will be ordered
         * @param isDESC order by ASC|DESC
         * @return the map has been ordered
         */
        public static <K, V> TreeMap<K, V> sortByKey(Map<K, V> map, boolean isDESC) {
            TreeMap result;
            if (isDESC) {
                result = new TreeMap(Collections.reverseOrder());
            } else {
                result = new TreeMap();
            }
            result.putAll(map);
            return result;
        }

        static class ValueComparatorASC<K, V> implements Comparator<K> {

            Map<K, V> base;

            public ValueComparatorASC(Map<K, V> base) {
                this.base = base;
            }

            @Override
            public int compare(K a, K b) {
                V o1 = base.get(a);
                V o2 = base.get(b);
                if (o1 instanceof Number) {
                    return Double.valueOf(o1.toString()) >= Double.valueOf(o2.toString()) ? 1 : -1;
                }
                return o1.hashCode() >= o2.hashCode() ? 1 : -1;
            }
        }

        static class ValueComparatorDESC<K, V> implements Comparator<K> {

            Map<K, V> base;

            public ValueComparatorDESC(Map<K, V> base) {
                this.base = base;
            }

            @Override
            public int compare(K a, K b) {
                V o1 = base.get(a);
                V o2 = base.get(b);
                if (o1 instanceof Number) {
                    return Double.valueOf(o1.toString()) >= Double.valueOf(o2.toString()) ? -1 : 1;
                }
                return o1.hashCode() >= o2.hashCode() ? -1 : 1;
            }
        }

        static class ValueCompareObjectFieldASC<K, V> implements Comparator<K> {

            Map<K, V> base;
            Field fields;

            public ValueCompareObjectFieldASC(Map<K, V> base, final String compareField, final Class<V> clazz) throws NoSuchFieldException {
                this.base = base;
                this.fields = clazz.getDeclaredField(compareField);
            }

            @Override
            public int compare(K a, K b) {
                V o1 = base.get(a);
                V o2 = base.get(b);
                try {
                    if (fields.getType().isAssignableFrom(Double.class)) {
                        return Double.valueOf(fields.get(o1).toString()) >= Double.valueOf(fields.get(o2).toString()) ? -1 : 1;
                    }
                    if (fields.getType().isAssignableFrom(Long.class)) {
                        return Long.valueOf(fields.get(o1).toString()) >= Long.valueOf(fields.get(o2).toString()) ? -1 : 1;
                    }
                    if (fields.getType().isAssignableFrom(Float.class)) {
                        return Float.valueOf(fields.get(o1).toString()) >= Float.valueOf(fields.get(o2).toString()) ? -1 : 1;
                    }
                    if (fields.getType().isAssignableFrom(Integer.class)) {
                        return Integer.valueOf(fields.get(o1).toString()) >= Integer.valueOf(fields.get(o2).toString()) ? -1 : 1;
                    }
                    if (fields.getType().isAssignableFrom(Short.class)) {
                        return Short.valueOf(fields.get(o1).toString()) >= Short.valueOf(fields.get(o2).toString()) ? -1 : 1;
                    }
                    if (fields.getType().isAssignableFrom(String.class)) {
                        return fields.get(o1).toString().compareTo(fields.get(o2).toString());
                    }
                    return fields.get(o1).hashCode() >= fields.get(o2).hashCode() ? -1 : 1;
                } catch (Exception ex) {
                }
                return 0;
            }
        }

        static class ValueCompareObjectFieldDESC<K, V> implements Comparator<K> {

            Map<K, V> base;
            Field fields;

            public ValueCompareObjectFieldDESC(Map<K, V> base, final String compareField, final Class<V> clazz) throws NoSuchFieldException {
                this.base = base;
                this.fields = clazz.getDeclaredField(compareField);
            }

            @Override
            public int compare(K a, K b) {
                V o1 = base.get(a);
                V o2 = base.get(b);
                try {
                    if (fields.getType().isAssignableFrom(Double.class)) {
                        return Double.valueOf(fields.get(o1).toString()) <= Double.valueOf(fields.get(o2).toString()) ? -1 : 1;
                    }
                    if (fields.getType().isAssignableFrom(Long.class)) {
                        return Long.valueOf(fields.get(o1).toString()) <= Long.valueOf(fields.get(o2).toString()) ? -1 : 1;
                    }
                    if (fields.getType().isAssignableFrom(Float.class)) {
                        return Float.valueOf(fields.get(o1).toString()) <= Float.valueOf(fields.get(o2).toString()) ? -1 : 1;
                    }
                    if (fields.getType().isAssignableFrom(Integer.class)) {
                        return Integer.valueOf(fields.get(o1).toString()) <= Integer.valueOf(fields.get(o2).toString()) ? -1 : 1;
                    }
                    if (fields.getType().isAssignableFrom(Short.class)) {
                        return Short.valueOf(fields.get(o1).toString()) <= Short.valueOf(fields.get(o2).toString()) ? -1 : 1;
                    }
                    if (fields.getType().isAssignableFrom(String.class)) {
                        return (-1) * fields.get(o1).toString().compareTo(fields.get(o2).toString());
                    }
                    return fields.get(o1).hashCode() <= fields.get(o2).hashCode() ? -1 : 1;
                } catch (Exception ex) {
                }
                return 0;
            }
        }
    }

    public static class Cookies {

        public static void setCookie(HttpServletResponse response, String cookieName, String strCookie, boolean isHttpOnly, boolean isSession, int expired, String domain) throws UnsupportedEncodingException {
            Cookie authCookie = new Cookie(cookieName, strCookie);
            authCookie.setHttpOnly(isHttpOnly);
            authCookie.setDomain(domain);
            authCookie.setPath("/");
            if (!isSession) {
                int expiration = 2592000;
                if (expired > 0) {
                    expiration = expired;
                }
                authCookie.setMaxAge(expiration);
            }

            response.addCookie(authCookie);
        }

        public static void removeCookie(HttpServletRequest request, HttpServletResponse response, String cookieName, String domain) throws UnsupportedEncodingException {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().compareToIgnoreCase(cookieName) == 0) {
                        cookie.setMaxAge(0);
                        cookie.setValue("");
                        cookie.setDomain(domain);
                        cookie.setPath("/");
                        response.addCookie(cookie);
                        break;
                    }
                }
            }
        }

        public static String getCookie(HttpServletRequest request, String cookieName) {
            Cookie[] cookies = request.getCookies();
            String strCookie = "";
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().compareTo(cookieName) == 0) {
                        strCookie = cookie.getValue();
                        break;
                    }
                }
            }
            return strCookie;
        }
    }

    public static class Links {

        public static Map<String, String> parse(URI uri) {
            return parse(uri, null);
        }

        public static Map<String, String> parse(String link) {
            return parse(link, null);
        }

        public static Map<String, String> parse(String link, String charset) {
            try {
                return parse(new URI(link), charset);
            } catch (URISyntaxException ex) {
                return Collections.emptyMap();
            }
        }

        public static Map<String, String> parse(URI uri, String charset) {
            String query = uri.getRawQuery();
            if (query == null || query.length() == 0) {
                return Collections.emptyMap();
            }
            Map<String, String> result = new HashMap<String, String>();
            Charset chs = null;
            try {
                chs = Charset.forName(charset);
            } catch (Exception e) {
            }
            Scanner scanner = new Scanner(query);
            scanner.useDelimiter("&");
            while (scanner.hasNext()) {
                String name;
                String value = "";
                String token = scanner.next();
                int i = token.indexOf("=");
                if (i != -1) {
                    name = decodeFormFields(token.substring(0, i).trim(), chs);
                    value = decodeFormFields(token.substring(i + 1).trim(), chs);
                } else {
                    name = decodeFormFields(token.trim(), chs);
                }
                result.put(name, value);
            }
            return result;
        }

        private static String decodeFormFields(String content, Charset charset) {
            if (content == null) {
                return null;
            }
            return Strings.urldecode(content, charset != null ? charset : Charset.forName("UTF-8"), true);
        }

        public HttpURLConnection send(String url, String contentType, String body, String authorizationKey, String method) throws IOException {
            if ((url == null) || (body == null)) {
                throw new IllegalArgumentException("arguments cannot be null");
            }
            if (Strings.isEmpty(body)) {
                body = "application/x-www-form-urlencoded;charset=UTF-8";
            }
            byte[] bytes = body.getBytes();
            HttpURLConnection conn = getConnection(url);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setFixedLengthStreamingMode(bytes.length);
            if (Strings.isEmpty(method)) {
                method = "GET";
            }
            conn.setRequestMethod(method);
            conn.setRequestProperty("Content-Type", contentType);
            if (!Strings.isEmpty(authorizationKey)) {
                conn.setRequestProperty("Authorization", "key=" + authorizationKey);
            }
            OutputStream out = conn.getOutputStream();
            out.write(bytes);
            out.close();
            return conn;
        }

        protected HttpURLConnection getConnection(String url) throws IOException {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            return conn;
        }
    }

    public static class IP {

        public List<IPRange> ranges = new ArrayList<IPRange>();

        public void add(long fromIP, long toIP) {
            ranges.add(new IPRange(fromIP, toIP));
            Collections.sort(ranges, new Comparator<IP.IPRange>() {

                @Override
                public int compare(IP.IPRange o1, IP.IPRange o2) {
                    if (o1.toIP < o2.fromIP) {
                        return -1;
                    }
                    if (o2.toIP < o1.fromIP) {
                        return 1;
                    }
                    return 0;
                }
            });
        }

        public void addAll(Collection<IPRange> data) {
            ranges.addAll(data);
            Collections.sort(ranges, new Comparator<IP.IPRange>() {

                @Override
                public int compare(IP.IPRange o1, IP.IPRange o2) {
                    if (o1.toIP < o2.fromIP) {
                        return -1;
                    }
                    if (o2.toIP < o1.fromIP) {
                        return 1;
                    }
                    return 0;
                }
            });
        }

        public boolean contains(String IPaddress) {
            long testIP = IPAddressToDecimal(IPaddress);
            return contains(testIP);
        }

        public boolean contains(long testIP) {
            for (IPRange ipRange : ranges) {
                if (ipRange.contains(testIP)) {
                    return true;
                }
            }
            return false;
        }

        public boolean contain(String IPaddress) {
            long testIP = IPAddressToDecimal(IPaddress);
            return contain(testIP);
        }

        public boolean contain(long testIP) {
            int left = 0;
            int right = ranges.size() - 1;
            int mid = 0;
            while (left <= right) {
                mid = (right + left) / 2;

                int check = localCheck(ranges.get(mid), testIP);
                if (check > 0) {
                    left = mid + 1;
                } else if (check < 0) {
                    right = mid - 1;
                } else {
                    return true;
                }
            }
            return false;
        }

        private static int localCheck(IPRange range, long ip) {
            if (range.fromIP > ip) {
                return -1;
            }
            if (range.toIP < ip) {
                return 1;
            }
            return 0;
        }

        public static class IPRange {

            public final long fromIP;
            public final long toIP;

            public IPRange(long fromIP, long toIP) {
                this.fromIP = fromIP;
                this.toIP = toIP;
            }

            public boolean contains(long testIP) {
                return testIP >= fromIP && testIP <= toIP;
            }
        }

        public static long IPAddressToDecimal(String ipAddress) {
            if (!validateIPAddress(ipAddress)) {
                return -1;
            }
            String[] addrArray = ipAddress.split("\\.");

            long num = 0;
            for (int i = 0; i < addrArray.length; i++) {
                int power = 3 - i;
                num += ((Integer.parseInt(addrArray[i]) % 256 * Math.pow(256, power)));
            }
            return num;
        }

        public static String DecimalToIPAddress(long i) {
            return ((i >> 24) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + (i & 0xFF);
        }

        public static boolean validateIPAddress(String IPAddress) {
            if (IPAddress.startsWith("0")) {
                return false;
            }

            if (IPAddress.isEmpty()) {
                return false;
            }
            return IPAddress.matches("\\A(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}\\z");
        }
    }

    public static class Regular {

        private static final Pattern userNamePattern;
        private static final Pattern passwordPattern;
        private static final Pattern emailPattern;
        private static final Pattern imagePattern;
        private static final Pattern cachePattern;
        private static final Pattern ipPattern;

        private static final String USERNAME_PATTERN = "^[a-zA-Z0-9_-]{6,50}$";
        private static final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&]).{6,})";
        private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        private static final String IMAGE_PATTERN = "([^\\s]+(\\.(?i)(jpg|png|gif|bmp))$)";
        private static final String CACHE_PATTERN = "^[A-Za-z0-9@\\\\-_\\\\.\\\\:]{1,100}$";
        private static final String IPADDRESS_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

        static {
            userNamePattern = Pattern.compile(USERNAME_PATTERN);
            passwordPattern = Pattern.compile(PASSWORD_PATTERN);
            emailPattern = Pattern.compile(EMAIL_PATTERN);
            imagePattern = Pattern.compile(IMAGE_PATTERN);
            cachePattern = Pattern.compile(CACHE_PATTERN);
            ipPattern = Pattern.compile(IPADDRESS_PATTERN);
        }

        public static boolean usernameValidator(final String userName) {
            return userNamePattern.matcher(userName).matches();
        }

        public static boolean passwordValidator(final String password) {
            return passwordPattern.matcher(password).matches();
        }

        public static boolean emailValidator(final String email) {
            return emailPattern.matcher(email).matches();
        }

        public static boolean imageValidator(final String image) {
            return imagePattern.matcher(image).matches();
        }

        public static boolean keyValidator(final String key) {
            return cachePattern.matcher(key).matches();
        }

        public static boolean ipValidator(final String ipAddress) {
            return ipPattern.matcher(ipAddress).matches();
        }
    }

    public static class Converts {

        public static <T extends Number> T toNumber(String data, Class<T> klass) {
            T result = null;
            try {
                switch (Switcher.fromString(klass.getName())) {
                    case SHORT:
                        result = (T) Short.valueOf(data);
                        break;
                    case INTEGER:
                        result = (T) Integer.valueOf(data);
                        break;
                    case LONG:
                        result = (T) Long.valueOf(data);
                        break;
                    case DOUBLE:
                        result = (T) Double.valueOf(data);
                        break;
                    case FLOAT:
                        result = (T) Float.valueOf(data);
                        break;
                }
            } catch (NumberFormatException e) {
            }
            return result;
        }

        public static <T extends Number> T toNumber(String data, T defaultValue, Class<T> klass) {
            if (Strings.isEmpty(data)) {
                return defaultValue;
            }
            T result = toNumber(data, klass);
            if (result == null) {
                result = defaultValue;
            }
            return result;
        }

        public static boolean toBoolean(String data) {
            if (data == null) {
                return false;
            }
            return "true".equalsIgnoreCase(data) || "1".equalsIgnoreCase(data);
        }

        public static boolean toBoolean(String data, boolean defaultValue) {
            if (data == null) {
                return defaultValue;
            }
            return "true".equalsIgnoreCase(data) || "1".equalsIgnoreCase(data);
        }

        public static String toString(Object obj) {
            if (obj == null) {
                return "";
            }
            return String.valueOf(obj);
        }

        public static String toString(Object obj, String defaultValue) {
            if (obj == null) {
                return defaultValue;
            }
            return String.valueOf(obj);
        }

        public static enum Switcher {

            FLOAT(Float.class),
            DOUBLE(Double.class),
            LONG(Long.class),
            INTEGER(Integer.class),
            SHORT(Short.class);

            private static final Map<String, Switcher> classNameToEnum = new HashMap<String, Switcher>();

            static {
                for (Switcher sw : values()) {
                    classNameToEnum.put(sw.className, sw);
                }
            }

            public static Switcher fromString(String className) {
                return classNameToEnum.get(className);
            }

            private String className;

            Switcher(Class<?> klass) {
                this.className = klass.getName();
            }

        }

    }

    public static class Strings {

        public static String compressString(String srcTxt) {
            try {
                ByteArrayOutputStream rstBao = new ByteArrayOutputStream();
                GZIPOutputStream zos = new GZIPOutputStream(rstBao);
                zos.write(srcTxt.getBytes());
                IOUtils.closeQuietly(zos);

                byte[] bytes = rstBao.toByteArray();
                return Base64.encodeBase64String(bytes);
            } catch (Exception e) {
            }
            return "";
        }

        public static String uncompressString(String zippedBase64Str) {
            String result = "";
            try {

                byte[] bytes = Base64.decodeBase64(zippedBase64Str);
                GZIPInputStream zi = null;
                try {
                    zi = new GZIPInputStream(new ByteArrayInputStream(bytes));
                    result = IOUtils.toString(zi);
                } finally {
                    IOUtils.closeQuietly(zi);
                }
            } catch (Exception e) {
            }
            return result;
        }

        public static boolean isEmpty(String data) {
            return data == null || data.length() == 0;
        }

        public static String nullToEmpty(String data) {
            return data == null ? "" : data.trim();
        }

        private static final String _str26 = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890";
        private static final int _len = 9;
        private static final String pattern = "###,###";

        public static String IDEncrypt(long id) {
            if (id <= 0L) {
                return "";
            }

            String tmp2 = ToString26(Long.toString(id).length());
            String tmp1 = Character.toString(_str26.toCharArray()[tmp2.length()]);
            String tmp3 = ToString26(JUtils.Converts.toNumber(FormatID(id), Long.class));
            return tmp1 + tmp2 + tmp3;
        }

        public static long IDDecrypt(String value) {
            try {
                if (value.length() <= 0) {
                    return -1L;
                }

                char tmp1 = value.toCharArray()[0];
                int index = _str26.indexOf(tmp1);
                if ((index <= 0) || (index > value.length())) {
                    return -1L;
                }

                long len = FromString26(value.substring(1, 1 + index));
                long userID = ToID(Long.toString(FromString26(value.substring(1 + index))));

                if (Long.toString(userID).length() != len) {
                    return -1L;
                }
                return userID;
            } catch (Exception ex) {
            }
            return -1L;
        }

        private static String FormatID(long id) {
            String sid = Long.toString(id);
            String str = "";
            long tmp = id % 10L;
            for (int i = 0; i < _len - sid.length(); i++) {
                tmp = Math.abs(Math.abs(_len - tmp - i));
                str = str + Long.toString(tmp);
            }
            str = Integer.toString(sid.length()) + sid + str;
            return str;
        }

        private static long ToID(String str) {
            if (str.length() < 10) {
                return -1L;
            }

            int l = JUtils.Converts.toNumber(str.substring(0, 1), -1, Integer.class);
            long id = JUtils.Converts.toNumber(str.substring(1, 1 + l), -1L, Long.class);

            long tmp = id % 10L;
            for (int i = l; i < str.length() - 1; i++) {
                tmp = Math.abs(Math.abs(9L - tmp) - (i - l));
                int cmp = JUtils.Converts.toNumber(str.toCharArray()[(i + 1)] + "", Integer.class);
                if (tmp != cmp) {
                    id = -1L;
                    break;
                }
            }
            return id;
        }

        private static String ToString26(long value) {
            String str = "";
            while (value >= _str26.length()) {
                long tmp = value % _str26.length();
                value /= _str26.length();
                str = Character.toString(_str26.toCharArray()[(int) tmp]) + str;
            }
            str = Character.toString(_str26.toCharArray()[(int) value]) + str;
            return str;
        }

        private static long FromString26(String str) {
            long value = 0L;
            long tmp = 1L;
            for (int i = 0; i < str.length(); i++) {
                long index = _str26.indexOf(Character.toString(str.toCharArray()[(str.length() - i - 1)]));
                if (index < 0L) {
                    return -1L;
                }
                value += index * tmp;
                tmp *= _str26.length();
            }
            return value;
        }

        public static String urlencode(String content, Charset charset, BitSet safechars, boolean blankAsPlus) {
            if (content == null) {
                return null;
            }
            StringBuilder buf = new StringBuilder();
            ByteBuffer bb = charset.encode(content);
            while (bb.hasRemaining()) {
                int b = bb.get() & 0xFF;
                if (safechars.get(b)) {
                    buf.append((char) b);
                } else if ((blankAsPlus) && (b == 32)) {
                    buf.append('+');
                } else {
                    buf.append("%");
                    char hex1 = Character.toUpperCase(Character.forDigit(b >> 4 & 0xF, 16));
                    char hex2 = Character.toUpperCase(Character.forDigit(b & 0xF, 16));
                    buf.append(hex1);
                    buf.append(hex2);
                }
            }
            return buf.toString();
        }

        public static String urldecode(String content, Charset charset, boolean plusAsBlank) {
            if (content == null) {
                return null;
            }
            ByteBuffer bb = ByteBuffer.allocate(content.length());
            CharBuffer cb = CharBuffer.wrap(content);
            while (cb.hasRemaining()) {
                char c = cb.get();
                if ((c == '%') && (cb.remaining() >= 2)) {
                    char uc = cb.get();
                    char lc = cb.get();
                    int u = Character.digit(uc, 16);
                    int l = Character.digit(lc, 16);
                    if ((u != -1) && (l != -1)) {
                        bb.put((byte) ((u << 4) + l));
                    } else {
                        bb.put((byte) 37);
                        bb.put((byte) uc);
                        bb.put((byte) lc);
                    }
                } else if ((plusAsBlank) && (c == '+')) {
                    bb.put((byte) 32);
                } else {
                    bb.put((byte) c);
                }
            }
            bb.flip();
            return charset.decode(bb).toString();
        }

        public static String formatNumberWithFlag(long value) {
            DecimalFormat myFormatter = new DecimalFormat(pattern);
            return myFormatter.format(value);
        }

        public static String normalize(String data) {
            if ((data != null) && (data.length() > 0)) {
                data = StringEscapeUtils.unescapeHtml(data);
                data = replaceHtmlEscapeNumber(data);
                data = removeNonPrintableCharactor(data).trim();
                data = Normalizer.normalize(data, Normalizer.Form.NFKC);
                return data.replaceAll("[ ]{1,}", " ");
            }
            return "";
        }

        public static String killUnicode(String data) {
            if ((data != null) && (data.length() > 0)) {
                data = data.replaceAll("Đ", "D");
                data = data.replaceAll("đ", "d");
                return Normalizer.normalize(data, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
            }
            return "";
        }

        public static String removeUnicode(String data) {
            if ((data != null) && (data.length() > 0)) {
                return data.replaceAll("[^\\p{ASCII}]", "");
            }
            return "";
        }

        public static String slug(String data) {
            data = killUnicode(data).trim();
            if (data.length() > 0) {
                data = data.replaceAll("[^a-zA-Z0-9- ]*", "");
                data = data.replaceAll("[ ]{1,}", "-").replaceAll("[-]{1,}", "-");
            }
            return data;
        }

        public static String slug(String data, int length) {
            String slugString = slug(data);
            return slug(data).substring(0, slugString.length() > length ? length : slugString.length());
        }

        public static String digitFormat(long value) {
            NumberFormat formatter = new DecimalFormat("#,##0");
            return formatter.format(value);
        }

        public static String capitalize(String data) {
            return capitalize(data, ' ', '.', '?', '(', ')', '{', '}', '[', ']', ':', ';', '\t', '\n', '\r');
        }

        public static String capitalize(String data, char... s) {
            return WordUtils.capitalize(data, s);
        }

        public static String capitalizeFully(String data, char... s) {
            return WordUtils.capitalizeFully(data, s);
        }

        public static String capitalizeFully(String data) {
            return capitalizeFully(data, ' ', '.', '?', '(', ')', '{', '}', '[', ']', ':', ';', '\t', '\n', '\r');
        }

        public static String removeNonPrintableCharactor(String data) {
            if (data == null || data.isEmpty()) {
                return "";
            }
            return data.replaceAll("\\p{C}", "");
        }

        public static String md5(String input) {
            if (input == null || input.isEmpty()) {
                return "";
            }
            input = removeNonPrintableCharactor(input).replaceAll("[ ]{1,}", "");
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(input.getBytes());

                byte byteData[] = md.digest();

                //convert the byte to hex format method 1
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < byteData.length; i++) {
                    sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
                }
                return sb.toString().toUpperCase();
            } catch (NoSuchAlgorithmException ex) {
                return "";
            }
        }

        public static String hmacSha1(String value, String key) {
            try {
                byte[] keyBytes = key.getBytes();
                SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA1");

                Mac mac = Mac.getInstance("HmacSHA1");
                mac.init(signingKey);

                byte[] rawHmac = mac.doFinal(value.getBytes());

                byte[] hexBytes = new Hex().encode(rawHmac);

                return new String(hexBytes, "UTF-8");
            } catch (Exception e) {
                return "";
            }
        }

        public static Set<String> getHashTag(String text) {
            Set<String> result = new HashSet<String>();
            if (text == null || text.isEmpty()) {
                return result;
            }
            text = normalize(text);
            StringTokenizer tokenizer = new StringTokenizer(text);
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                if ('#' == token.charAt(0)) {
                    result.add(token);
                }
            }
            return result;
        }

        public static String appendTagLink(String text, String linkFormat, String strClass) {
            StringBuilder result = new StringBuilder();
            if (text == null || text.isEmpty()) {
                return "";
            }
            text = normalize(text);
            StringTokenizer tokenizer = new StringTokenizer(text);
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                if ('#' == token.charAt(0)) {
                    token = token.replaceAll("#", "");
                    result.append("<a href=\"").append(String.format(linkFormat, slug(token)))
                            .append("\" titlte=\"Tag: ").append(token).append("\" class=\"")
                            .append(strClass).append("\">").append(token).append("</a>").append(" ");
                } else {
                    result.append(token).append(" ");
                }
            }
            return result.toString();
        }

        private static final Pattern htmlNumber = Pattern.compile("\\&\\#\\d+;?");

        public static String replaceHtmlEscapeNumber(String str) {
            if (str == null) {
                return null;
            }
            Matcher matcher = htmlNumber.matcher(str);
            while (matcher.find()) {
                String tmp = matcher.group();
                int pos = matcher.start();
                int end = matcher.end();
                int number = Integer.parseInt(tmp.replaceAll("[^0-9]*", ""));
                char ch = (char) number;
                str = str.substring(0, pos) + ch + str.substring(end);
                matcher = htmlNumber.matcher(str);
            }

            return str;
        }

        public static String toHtmlEscapeNumber(String str) {
            if (str == null) {
                return null;
            }
            StringBuilder result = new StringBuilder();
            char[] chs = str.toCharArray();
            for (char ch : chs) {
                result.append("&#").append((int) ch);
                System.out.print("&#");
                System.out.print((int) ch);
            }

            return str;
        }

        public static String removeXSS(String input) {
            if (input == null || input.isEmpty()) {
                return "";
            }
            input = replaceHtmlEscapeNumber(input);
            input = normalize(input);
            input = input.trim().replaceAll("(\\n)(<\\s*br[^>]*>)?{1,}", "<br />").replaceAll("(\\n)(<\\s*p[^>]*>)?{1,}", "<p>");
            input = Jsoup.clean(input, Whitelist.none().addTags(new String[]{"br", "p"}));
            return input;
        }

        public static String removeTag(String input, String tag) {
            if (input == null || input.isEmpty()) {
                return "";
            }
            input = input.replaceAll("<\\s*(?i)" + tag + "[^>]*>.*?</\\s*(?i)" + tag + "[^>]*>", "");
            return input;
        }

        public static List<String> toList(String str, String regex, boolean useRegex) {
            List<String> list = new ArrayList<String>();

            if (str != null) {
                if (useRegex) {
                    list = Arrays.asList(str.split(regex));
                } else {
                    list = Arrays.asList(str.split("\\s*[" + regex + "]+\\s*"));
                }
            }
            return list;
        }

        public static List<String> toList(String str, String regex) {
            List<String> list = new ArrayList<String>();

            if (str != null) {
                list = Arrays.asList(str.split("\\s*[" + regex + "]+\\s*"));
            }
            return list;
        }

        public static List<String> toList(String str, String regex, String matcherRegex) {
            List<String> list = new ArrayList<String>();
            Pattern currPattern = Pattern.compile(matcherRegex, Pattern.CASE_INSENSITIVE);
            Matcher matcher;
            if (str != null) {
                String[] tmp = str.split("\\s*[" + regex + "]+\\s*");
                for (String item : tmp) {
                    matcher = currPattern.matcher(item);

                    while (matcher.find()) {
                        list.add(matcher.group(1));
                    }
                }
            }
            return list;
        }

        public static <T extends Number> List<T> toList(String str, String regex, Class<T> Klass) {
            List<T> list = new ArrayList<T>();
            if (str != null) {
                String[] datas = str.split("\\s*[" + regex + "]+\\s*");
                switch (Converts.Switcher.fromString(Klass.getName())) {
                    case SHORT:
                        for (String data : datas) {
                            try {
                                list.add((T) Short.valueOf(data));
                            } catch (NumberFormatException e) {
                            }
                        }
                        break;
                    case INTEGER:
                        for (String data : datas) {
                            try {
                                list.add((T) Integer.valueOf(data));
                            } catch (NumberFormatException e) {
                            }
                        }
                        break;
                    case LONG:
                        for (String data : datas) {
                            try {
                                list.add((T) Long.valueOf(data));
                            } catch (NumberFormatException e) {
                            }
                        }
                        break;
                    case FLOAT:
                        for (String data : datas) {
                            try {
                                list.add((T) Float.valueOf(data));
                            } catch (NumberFormatException e) {
                            }
                        }
                        break;
                    case DOUBLE:
                        for (String data : datas) {
                            try {
                                list.add((T) Double.valueOf(data));
                            } catch (NumberFormatException e) {
                            }
                        }
                        break;
                }
            }
            return list;
        }

        public static String getString(InputStream stream) throws IOException {
            if (stream == null) {
                return "";
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            StringBuilder content = new StringBuilder();
            String newLine;
            do {
                newLine = reader.readLine();
                if (newLine != null) {
                    content.append(newLine).append('\n');
                }
            } while (newLine != null);
            if (content.length() > 0) {
                content.setLength(content.length() - 1);
            }
            return content.toString();
        }

    }

    public static class Lists {

        public static <T> List<T> merge(List<T> list1, List<T> list2) {
            List<T> result = new ArrayList<T>();
            if (list1 == null) {
                list1 = new ArrayList<T>();
            }
            if (list2 == null) {
                list2 = new ArrayList<T>();
            }
            Set<T> set = new HashSet<T>();
            while (list1.size() > 0 || list2.size() > 0) {
                if (list1.size() > 0) {
                    T tmp = list1.get(0);
                    if (set.add(tmp)) {
                        result.add(tmp);
                    }
                    list1.remove(tmp);
                }
                if (list2.size() > 0) {
                    T tmp = list2.get(0);
                    if (set.add(tmp)) {
                        result.add(tmp);
                    }
                    list2.remove(tmp);
                }
            }
            return result;
        }

        public static <T> void removeDuplicate(List<T> list) {
            if (list == null) {
                return;
            }
            HashSet<T> h = new HashSet<T>(list);
            list.clear();
            list.addAll(h);
        }

        public static <T> void removeDuplicateKeepOrder(List<T> list) {
            Set<T> set = new HashSet<T>();
            List<T> newList = new ArrayList<T>();
            for (Object element : list) {
                if (set.add((T) element)) {
                    newList.add((T) element);
                }
            }
            list.clear();
            list.addAll(newList);
        }

        public static <T> List<T> pagging(List<T> data, int offset, int count) {
            List<T> result = new ArrayList<T>();
            if (data != null) {
                int lsCount = data.size();
                if (offset > lsCount) {
                    return result;
                }
                int fromIndex = offset < 0 ? 0 : offset;
                int toIndex = offset + count;
                toIndex = toIndex > lsCount ? lsCount : toIndex;
                result = data.subList(fromIndex, toIndex);
            }
            return result;
        }

        public static <T extends Number> List<T> fromListString(List<String> datas, Class<T> klass) {
            List<T> list = new ArrayList<T>();
            switch (Converts.Switcher.fromString(klass.getName())) {
                case SHORT:
                    for (String data : datas) {
                        try {
                            list.add((T) Short.valueOf(data));
                        } catch (NumberFormatException e) {
                        }
                    }
                    break;
                case INTEGER:
                    for (String data : datas) {
                        try {
                            list.add((T) Integer.valueOf(data));
                        } catch (NumberFormatException e) {
                        }
                    }
                    break;
                case LONG:
                    for (String data : datas) {
                        try {
                            list.add((T) Long.valueOf(data));
                        } catch (NumberFormatException e) {
                        }
                    }
                    break;
                case FLOAT:
                    for (String data : datas) {
                        try {
                            list.add((T) Float.valueOf(data));
                        } catch (NumberFormatException e) {
                        }
                    }
                    break;
                case DOUBLE:
                    for (String data : datas) {
                        try {
                            list.add((T) Double.valueOf(data));
                        } catch (NumberFormatException e) {
                        }
                    }
                    break;
            }
            return list;
        }

        public static <T> List<String> toListString(List<T> datas) {
            List<String> result = new ArrayList<String>();
            for (T data : datas) {
                result.add(data.toString());
            }
            return result;
        }

        public static String join(Iterator iterator, String separator) {
            if (iterator == null) {
                return "";
            }
            if (!iterator.hasNext()) {
                return "";
            }
            Object first = iterator.next();
            if (!iterator.hasNext()) {
                return first == null ? "" : first.toString();
            }

            StringBuilder buf = new StringBuilder();
            if (first != null) {
                buf.append(first);
            }

            while (iterator.hasNext()) {
                if (separator != null) {
                    buf.append(separator);
                }
                Object obj = iterator.next();
                if (obj != null) {
                    buf.append(obj);
                }
            }
            return buf.toString();
        }

        public static String join(Collection collection, String separator) {
            if (collection == null) {
                return "";
            }
            return join(collection.iterator(), separator);
        }

        public static <T> List<List<T>> partition(List<T> list, int size) {
            if (list == null || list.isEmpty()) {
                return new ArrayList<List<T>>();
            }

            return (list instanceof RandomAccess)
                    ? new RandomAccessPartition<T>(list, size)
                    : new Partition<T>(list, size);
        }

        private static class Partition<T> extends AbstractList<List<T>> {

            final List<T> list;
            final int size;

            Partition(List<T> list, int size) {
                this.list = list;
                this.size = size;
            }

            @Override
            public List<T> get(int index) {
                int start = index * size;
                int end = Math.min(start + size, list.size());
                return list.subList(start, end);
            }

            @Override
            public int size() {
                // TODO(user): refactor to common.math.IntMath.divide
                int result = list.size() / size;
                if (result * size != list.size()) {
                    result++;
                }
                return result;
            }

            @Override
            public boolean isEmpty() {
                return list.isEmpty();
            }
        }

        private static class RandomAccessPartition<T> extends Partition<T>
                implements RandomAccess {

            RandomAccessPartition(List<T> list, int size) {
                super(list, size);
            }
        }

    }

    public static class Dates {

        private static final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        public static Date fromString(String data, String format) {
            try {
                return new SimpleDateFormat(format).parse(data);
            } catch (ParseException e) {
            }
            return null;
        }

        public static Date fromString(String data) {
            try {
                return formatter.parse(data);
            } catch (ParseException e) {
            }
            return null;
        }

        public static String toString(Date date) {
            try {
                return formatter.format(date);
            } catch (Exception e) {
            }
            return "";
        }

        public static String toString(Date date, String format) {
            try {
                return new SimpleDateFormat(format).format(date);
            } catch (Exception e) {
            }
            return "";
        }

        public static String toString(long time, String format) {
            Date date = new Date(time);
            try {
                return new SimpleDateFormat(format).format(date);
            } catch (Exception e) {
            }
            return "";
        }
    }

    public static class FixedList<E> extends ArrayList<E> {

        private final int limit;

        public FixedList(int limit) {
            this.limit = limit;
        }

        @Override
        public boolean add(E item) {
            if (this.size() >= this.limit) {
                return false;
            }
            super.add(item);
            return true;
        }

        @Override
        public void add(int index, E item) {
            if (index >= this.limit) {
                return;
            }
            super.add(index, item);
            while (this.size() > this.limit) {
                super.remove(this.size() - 1);
            }
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            if (this.size() >= this.limit) {
                return false;
            }
            super.addAll(c);
            while (this.size() > this.limit) {
                super.remove(this.size() - 1);
            }
            return true;
        }

        @Override
        public boolean addAll(int index, Collection<? extends E> c) {
            if (index >= this.limit) {
                return false;
            }
            super.addAll(index, c);
            while (this.size() > this.limit) {
                super.remove(this.size() - 1);
            }
            return true;
        }

    }

    public static class Files {

        public static String read(String filePath, String defaultValue) {
            StringBuilder sb = new StringBuilder();
            BufferedReader br = null;
            try {
                String sCurrentLine;
                br = new BufferedReader(new FileReader(filePath));
                while ((sCurrentLine = br.readLine()) != null) {
                    sb.append(sCurrentLine).append("\n");
                }

            } catch (IOException e) {
            } finally {
                try {
                    if (br != null) {
                        br.close();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            if (sb.length() == 0) {
                if (defaultValue != null) {
                    sb.append(defaultValue);
                }
            }
            return sb.toString();
        }
    }
}
