package ru.nchernetsov.mvc;

import java.sql.SQLException;
import java.util.Collection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import ru.nchernetsov.database_mapper.entity.User;
import ru.nchernetsov.database_mapper.orm.ORM;

public class Controller extends AbstractController {

  private final ORM orm;

  public Controller() throws SQLException {
    orm = new ORM();

    orm.execQuery("TRUNCATE TABLE users;");

    orm.save(new User(1, "Ivan", 25));
    orm.save(new User(2, "Maria", 19));
    orm.save(new User(3, "Nikita", 31));
  }

  @Override
  protected ModelAndView handleRequestInternal(HttpServletRequest request,
      HttpServletResponse response) {

    ModelAndView model = new ModelAndView("users");

    Collection<User> users = orm.loadAll(User.class);

    model.addObject("users", users);

    return model;
  }
}
