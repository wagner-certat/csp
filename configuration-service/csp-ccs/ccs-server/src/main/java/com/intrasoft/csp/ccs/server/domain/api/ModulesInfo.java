package com.intrasoft.csp.ccs.server.domain.api;


import java.util.List;

public class ModulesInfo {

    private List<ModuleInfo> modules;

    public List<ModuleInfo> getModules() {
        return modules;
    }

    public void setModules(List<ModuleInfo> modules) {
        this.modules = modules;
    }


    @Override
    public String toString() {
        return "ModulesInfo{" +
                "modules=" + modules +
                '}';
    }
}
