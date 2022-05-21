package zuev.nikita.message;

import zuev.nikita.structure.Organization;

import java.io.Serializable;

public class ClientRequest implements Serializable {
    private final String[] fullCommand;
    private final Organization organization;
    private final String path;

    public ClientRequest(String[] fullCommand, Organization organization, String path) {
        this.fullCommand = fullCommand;
        this.organization = organization;
        this.path = path;
    }

    public Organization getOrganization() {
        return organization;
    }

    public String[] getFullCommand() {
        return fullCommand;
    }

    public String getPath() {
        return path;
    }
}
