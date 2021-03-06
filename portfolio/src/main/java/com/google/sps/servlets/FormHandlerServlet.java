package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.KeyFactory;

import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.OrderBy;
import com.google.gson.Gson;
import com.google.sps.data.Info;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;


@WebServlet("/form-handler")
public class FormHandlerServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    String text = Jsoup.clean(request.getParameter("text-input"), Whitelist.none());
    long timestamp = System.currentTimeMillis();

    System.out.println("You submitted: " + text);

    Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    KeyFactory keyFactory = datastore.newKeyFactory().setKind("Task");
    FullEntity taskEntity =
        Entity.newBuilder(keyFactory.newKey())
            .set("title", text)
            .set("timestamp", timestamp)
            .build();
    datastore.put(taskEntity);

    response.sendRedirect("/index.html");
  }
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    Query<Entity> query =
        Query.newEntityQueryBuilder().setKind("Task").setOrderBy(OrderBy.desc("timestamp")).build();
    QueryResults<Entity> results = datastore.run(query);

    List<Info> information = new ArrayList<>();
    while (results.hasNext()) {
      Entity entity = results.next();

      long id = entity.getKey().getId();
      String title = entity.getString("title");
      long timestamp = entity.getLong("timestamp");

      Info info = new Info(id, title, timestamp);
      information.add(info);
    }

    Gson gson = new Gson();

    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(information));
  }
}