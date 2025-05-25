package bandung.se;

/**
 * 配置MBean
 *
 * @author iMinusMinus
 * @date 2024-05-04
 */
public interface ConfigMXBean {

    /**
     * 获取配置
     *
     * @param name 配置名称
     * @return 配置值
     */
    String getProperty(String name);

}
