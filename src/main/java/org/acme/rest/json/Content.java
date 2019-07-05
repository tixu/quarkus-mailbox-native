package org.acme.rest.json;
import io.quarkus.runtime.annotations.RegisterForReflection;
@RegisterForReflection
public class Content {
    private String id ;
    private String name;
    private String accessKey;
    private String state;


    public Content() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "Content [accessKey=" + accessKey + ", id=" + id + ", name=" + name + ", state=" + state + "]";
    }

   
    
}