package bandung.se;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Objects;

public abstract class Utils {

    public static <T> boolean containsAll(T[] provided, T[] request) {
        for (int i = 0; i < request.length; i++) {
            boolean found = false;
            for (int j = 0; j < provided.length; j++) {
                if (Objects.equals(provided[j], request[i])) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return true;
            }
        }
        return false; // 数量较少，无需用Set或移除已匹配项
    }

    public static <T> T[] retainAll(T[] container, T[] retainable) {
        T[] result = Arrays.copyOf(retainable, retainable.length);
        int matches = 0;
        for (int i = 0; i < retainable.length; i++) {
            for (int j = 0; j < container.length; j++) {
                if (Objects.equals(retainable[i], container[j])) {
                    result[matches++] = retainable[i];
                    break;
                }
            }
        }
        return Arrays.copyOf(result, matches);
    }

    public static InetAddress parseIpAddress(String ip) {
        if (ip == null || ip.length() == 0) {
            return null;
        }
        byte[] addr;
        if (ip.indexOf(":") >= 0) {
            addr = textToNumericFormatV6(ip);
        } else {
            addr = textToNumericFormatV4(ip);
        }
        try {
            return InetAddress.getByAddress(addr);
        } catch (UnknownHostException uhe) {
            throw new RuntimeException(uhe.getMessage(), uhe);
        }
    }

    /**
     * IP字符串转为数字格式
     * @see sun.net.util.IPAddressUtil#textToNumericFormatV4(java.lang.String)
     * @param ip IP字符串
     * @return 数字格式IP
     */
    public static byte[] textToNumericFormatV4(String ip) {
        if (ip == null || ip.length() == 0) {
            return null;
        }
        if (ip.length() < 7 || ip.length() > 15) {
            throw new IllegalArgumentException("illegal IPv4 Address: " + ip);
        }
        int currByte = 0;
        byte[] addr = new byte[4];
        int j = 0;
        for (int i = 0; i < ip.length(); i++) {
            char c = ip.charAt(i);
            if (c == '.') {
                if (currByte < 0 || currByte > 0xFF || i == ip.length() - 1) {
                    throw new IllegalArgumentException("illegal IPv4 Address: " + ip);
                }
                addr[j++] = (byte) (currByte & 0xFF);
                currByte = 0;
            } else {
                currByte *= 10;
                currByte += Character.digit(c, 10);
            }
        }
        if (currByte < 0 || currByte > 0xFF) {
            throw new IllegalArgumentException("illegal IPv4 Address: " + ip);
        }
        addr[j++] = (byte) (currByte & 0xFF);
        if (j != addr.length) {
            throw new IllegalArgumentException("illegal IPv4 Address: " + ip);
        }
        return addr;
    }

    /**
     * IP字符串转为数字格式
     * @see sun.net.util.IPAddressUtil#textToNumericFormatV6(java.lang.String)
     * @param ip IP字符串
     * @return 数字格式IP
     */
    public static byte[] textToNumericFormatV6(String ip) {
        if (ip == null || ip.length() == 0) {
            return null;
        }
        if (ip.length() < 3 || ip.length() > 48) { // ::1, 1234:5678:9012:3456:7890:1234:255.255.255.255%10
            throw new IllegalArgumentException("illegal IPv6 Address: " + ip);
        }
        int pp = ip.lastIndexOf("%");
        if (pp == ip.length() - 1) {
            throw new IllegalArgumentException("illegal IPv6 Address: " + ip);
        } else if (pp < 0) {
            pp = ip.length();
        }
        char[] chars = ip.toCharArray();
        byte[] addr = new byte[16];
        int index = 0;
        int val = 0;
        int lastColonPosition = 0;
        int i = 0;
        for (; i < pp; i++) {
            if (chars[i] == ':') {
                if (val < 0 || val > 0xFFFF || (i == 0 && chars[i + 1] != ':')) {
                    throw new IllegalArgumentException("illegal IPv6 Address: " + ip);
                }
                addr[index++] = (byte) ((val >> 8) & 0xFF);
                addr[index++] = (byte) (val & 0xFF);
                val = 0;
                lastColonPosition = i;
                if (chars[i + 1] == ':') {
                    break;
                }
            } else if (chars[i] == '.') { // IPv6内嵌IPv4地址
                byte[] v4 = textToNumericFormatV4(new String(chars, lastColonPosition + 1, pp - lastColonPosition - 1));
                System.arraycopy(v4, 0, addr, 12, v4.length);
                index += 4;
            } else {
                val <<= 4;
                val |= Character.digit(chars[i], 16);
            }
        }
        if (i == pp) {
            if (val < 0 || val > 0xFFFF) {
                throw new IllegalArgumentException("illegal IPv6 Address: " + ip);
            }
            addr[index++] = (byte) ((val >> 8) & 0xFF);
            addr[index++] = (byte) (val & 0xFF);
        }
        val = 0;
        int position = addr.length;
        boolean allowV4 = true, v4Inside = false;
        int tail = pp -1;
        for (; tail > i; tail--) {
            if (chars[tail] == '.') {
                if (!allowV4) {
                    throw new IllegalArgumentException("illegal IPv6 Address: " + ip);
                }
                v4Inside = true;
                addr[--position] = (byte) (reverseHexGroupToInt(val) & 0xFF);
                val = 0;
            } else if (chars[tail] == ':') {
                if (allowV4 && v4Inside) {
                    if (position != addr.length - 3) {
                        throw new IllegalArgumentException("illegal IPv6 Address: " + ip);
                    }
                    addr[--position] = (byte) (reverseHexGroupToInt(val) & 0xFF);
                    allowV4 = false;
                    val = 0;
                    continue;
                }
                if (val < 0 || val > 0xFFFF) {
                    throw new IllegalArgumentException("illegal IPv6 Address: " + ip);
                }
                if ((val & 0xF000) != 0) {
                    addr[--position] = (byte) ((val >> 12) | (((val >> 8) & 0xF) << 4));
                } else if ((val & 0x0F00) != 0){
                    addr[--position] = (byte) ((val >> 8) & 0xF);
                }
                if ((val & 0x00F0) != 0) {
                    addr[--position] = (byte) (((val & 0xFF) >> 4) | ((val & 0xF) << 4));
                } else {
                    addr[--position] = (byte) (val & 0xF);
                }
                val = 0;
                if (chars[tail -1] == ':') {
                    tail--;
                    break;
                }
            } else {
                val <<= 4;
                val |= Character.digit(chars[tail], 16);
            }
        }
        if (position < index || (position != addr.length && tail != i)) {
            throw new IllegalArgumentException("illegal IPv6 Address: " + ip);
        }
        return addr;
    }

    private static int reverseHexGroupToInt(int val) {
        int high = 1, middle = 1;
        if ((val & 0xF00) != 0) {
            high = 100;
            middle = 10;
        } else if ((val & 0xF0) != 0) {
            high = 10;
            middle = 1;
        }
        int result =  (val >> 8) + (((val >> 4) & 0xF) * middle) + ((val & 0xF) * high);
        if (result < 0 || result > 0xFF) {
            throw new IllegalArgumentException("illegal IPv6 Address");
        }
        return result;
    }
}
