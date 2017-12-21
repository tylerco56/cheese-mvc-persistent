package org.launchcode.controllers;

import org.launchcode.models.Category;
import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@Controller
@RequestMapping(value = "menu")
public class MenuController {

    @Autowired
    MenuDao menuDao;

    @Autowired
    CheeseDao cheeseDao;

    @RequestMapping(value = "")
    public String index(Model model) {

        model.addAttribute("menus", menuDao.findAll());
        model.addAttribute("title", "My Menus");

        return "menu/index";
    }

    @RequestMapping(value = "view/{menuId}")
    public String view(Model model, @PathVariable("menuId") Integer menuId) {

        model.addAttribute("title", menuDao.findOne(menuId).getName());
        model.addAttribute("menu", menuDao.findOne(menuId));

        return "menu/view";
    }

    @RequestMapping(value = "add")
    public String add(Model model) {

        model.addAttribute("title", "Add Menu");
        model.addAttribute(new Menu());

        return "menu/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String add(Model model,
                      @ModelAttribute @Valid Menu menu, Errors errors) {

        if (errors.hasErrors()) {
            model.addAttribute(menu);
            return "category/add";
        }

        menuDao.save(menu);

        return "redirect:view/" + menu.getId();
    }

    @RequestMapping(value = "add-item/{menuId}")
    public String addItem(Model model,
                          @PathVariable("menuId") Integer menuId) {

        Menu menu = menuDao.findOne(menuId);
        Iterable<Cheese> cheeses = cheeseDao.findAll();
        AddMenuItemForm addMenuItemForm = new AddMenuItemForm(menu, cheeses);

        model.addAttribute("title", "Add item to menu:" + menu.getName());
        model.addAttribute("form", addMenuItemForm);

        return "menu/add-item";
    }

    @RequestMapping(value = "add-item", method = RequestMethod.POST)
    public String processAddItem(Model model,
                                 @ModelAttribute @Valid AddMenuItemForm addMenuItemForm,
                                 Errors errors) {

        //if (errors.hasErrors()){
        //    model.addAttribute(addMenuItemForm);
        //    model.addAttribute("title", "Add item to menu:" + addMenuItemForm.getMenu().getName());
        //    return "menu/add-item/" + addMenuItemForm.getMenuId();
        //}


        Menu menu = menuDao.findOne(addMenuItemForm.getMenuId());
        Cheese cheese = cheeseDao.findOne(addMenuItemForm.getCheeseId());

        menu.addItem(cheese);
        menuDao.save(menu);


        return "redirect:view/" + menu.getId();
    }
}