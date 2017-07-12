package com.instrasoft.csp.ccs.controller;

import com.instrasoft.csp.ccs.config.DataContextUrl;
import com.instrasoft.csp.ccs.config.PagesContextUrl;
import com.instrasoft.csp.ccs.domain.postgresql.Csp;
import com.instrasoft.csp.ccs.domain.postgresql.CspIp;
import com.instrasoft.csp.ccs.repository.CspContactRepository;
import com.instrasoft.csp.ccs.repository.CspIpRepository;
import com.instrasoft.csp.ccs.repository.CspRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class MvcController implements PagesContextUrl, DataContextUrl {

    private static final Logger LOG = LoggerFactory.getLogger(MvcController.class);


    @Autowired
    CspRepository cspRepository;

    @Autowired
    CspContactRepository cspContactRepository;

    @Autowired
    CspIpRepository cspIpRepository;

//    @Autowired
//    private ErrorAttributes errorAttributes;

    /*
    MAIN Pages
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView index(Model model) {
        model = this.init(model);
        model.addAttribute("dataDashboardUrl", DATA_BASEURL+DATA_DASHBOARD);
        model.addAttribute("navHomeClassActive", "active");
        return new ModelAndView("pages/dashboard", "dashboard", model);
    }

    @RequestMapping(value = PAGES_DASHBOARD, method = RequestMethod.GET)
    public ModelAndView dashboard(Model model) {
        model = this.init(model);
        model.addAttribute("dataDashboardUrl", DATA_BASEURL+DATA_DASHBOARD);
        model.addAttribute("navHomeClassActive", "active");
        return new ModelAndView("pages/dashboard", "dashboard", model);
    }

    @RequestMapping(value = PAGES_MANAGE, method = RequestMethod.GET)
    public ModelAndView manage(@RequestParam("cspId") String cspId, Model model) {
        model = this.init(model);
        //model.addAttribute("navHomeClassActive", "");
        return new ModelAndView("pages/manage", "manage", model);
    }


    /*
    CSP Pages
     */
    @RequestMapping(value = PAGES_CSP_LIST, method = RequestMethod.GET)
    public ModelAndView cspList(Model model) {
        model = this.init(model);
        model.addAttribute("addCspUrl", PAGES_CSP_REGISTER);
        model.addAttribute("dataCspUrl", DATA_BASEURL + DATA_CSPS);
        model.addAttribute("removeCspUrl", DATA_BASEURL + DATA_CSP_REMOVE);
        model.addAttribute("navCspClassActive", "active");
        return new ModelAndView("pages/csp/list", "list", model);
    }

    @RequestMapping(value = PAGES_CSP_REGISTER, method = RequestMethod.GET)
    public ModelAndView cspRegister(Model model) {
        model = this.init(model);
        model.addAttribute("cspSaveUrl", DATA_BASEURL + DATA_CSP_SAVE);
        model.addAttribute("cspListUrl", PAGES_CSP_LIST);
        model.addAttribute("navCspClassActive", "active");
        return new ModelAndView("pages/csp/register", "register", model);
    }

    @RequestMapping(value = PAGES_CSP_UPDATE, method = RequestMethod.GET)
    public ModelAndView cspUpdate(@RequestParam("cspId") String cspId, Model model) {
        model = this.init(model);

        Csp csp = cspRepository.findOne(cspId);
        if (csp == null) {
            return new ModelAndView("error", "error", model);
        }

        model.addAttribute("cspUpdateUrl", DATA_BASEURL + DATA_CSP_UPDATE);
        model.addAttribute("cspListUrl", PAGES_CSP_LIST);

        model.addAttribute("cspData", cspRepository.findOne(cspId));
        model.addAttribute("cspContacts", cspContactRepository.findByCspId(cspId));
        model.addAttribute("cspInternalIps", cspIpRepository.findByCspIdAndExternal(cspId, 0));
        model.addAttribute("cspExternalIps", cspIpRepository.findByCspIdAndExternal(cspId, 1));

        model.addAttribute("navCspClassActive", "active");
        return new ModelAndView("pages/csp/update", "update", model);
    }



    /*
    MODULE PAGES
     */
    @RequestMapping(value = PAGES_MODULE_LIST, method = RequestMethod.GET)
    public ModelAndView moduleList(Model model) {
        model = this.init(model);
        model.addAttribute("addModuleUrl", PAGES_MODULE_REGISTER);
        model.addAttribute("dataModuleUrl", DATA_BASEURL + DATA_MODULES);
        model.addAttribute("navModuleClassActive", "active");
        return new ModelAndView("pages/module/list", "list", model);
    }

    @RequestMapping(value = PAGES_MODULE_REGISTER, method = RequestMethod.GET)
    public ModelAndView moduleRegister(Model model) {
        model = this.init(model);
        model.addAttribute("moduleSaveUrl", DATA_BASEURL + DATA_MODULE_SAVE);
        model.addAttribute("moduleListUrl", PAGES_MODULE_LIST);
        model.addAttribute("navModuleClassActive", "active");
        return new ModelAndView("pages/module/register", "register", model);
    }



    /*
    Internal methods
     */
    private Model init(Model m) {
        m.addAttribute("dashboardUrl", PAGES_DASHBOARD);
        m.addAttribute("cspListUrl", PAGES_CSP_LIST);
        m.addAttribute("moduleListUrl", PAGES_MODULE_LIST);

        return m;
    }
}
