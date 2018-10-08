package pl.edu.agh.iosr.raft.node.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties("raft")
@EnableConfigurationProperties
public class RaftProperties {

    private String name;
    private List<String> nodes = new ArrayList<>();

    public String getName() {
        return name;
    }

    public List<String> getNodes() {
        return nodes;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNodes(List<String> nodes) {
        this.nodes = nodes;
    }
}
