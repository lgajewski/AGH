package lgajewski.distributed;

public enum JMSProperties {

    JNDI_CONTEXT_FACTORY_CLASS_NAME("org.exolab.jms.jndi.InitialContextFactory"),
    DEFAULT_JMS_PROVIDER_URL("tcp://localhost:3035/"),
    DEFAULT_QUEUE_NAME("q-task");

    private String property;

    JMSProperties(String defaultProperty) {
        this.property = defaultProperty;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getProperty() {
        return property;
    }
}
