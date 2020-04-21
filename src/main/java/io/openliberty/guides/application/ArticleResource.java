package io.openliberty.guides.application;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.mongodb.client.MongoClient;

import org.bson.Document;
import org.eclipse.microprofile.opentracing.Traced;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;

import io.openliberty.guides.application.client.UnknownUriException;
import io.openliberty.guides.application.client.UnknownUriExceptionMapper;
import io.openliberty.guides.application.models.Article;
import io.openliberty.guides.application.util.DBManager;
import io.openliberty.guides.application.util.Serializer;
import io.openliberty.guides.application.util.UserManager;
import io.openliberty.guides.application.util.UserManager.CacheObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@RegisterProvider(UnknownUriExceptionMapper.class)
@RequestScoped
@Path("/Article")
public class ArticleResource {
    private static final String AUTHOR = "author";
    private static final String TITLE = "title";
    private static final String BODY = "body";
    private static final String DATE = "date";


    @POST
    @Path("/Create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createArticle(@CookieParam(UserManager.COOKIEOFTHEGODS) Cookie cookie, Article article) throws UnknownUriException {
        String key = UserManager.checkCache(cookie, article.getKey());;
        
        if (key == null) {
            return Response.status(Response.Status.OK).entity(DBManager.NOTLOGGEDIN.toJson()).build();
        }

        if (UserManager.JEDIS.get(key) == null) {
            return Response.status(Response.Status.OK).entity(DBManager.NOTLOGGEDIN.toJson()).build();
        }

        System.out.println("Creating Article ... " + ArticleResource.class.getSimpleName() + " [59]");
        UserManager.CacheObject cacheObject;

        try {
            cacheObject = (CacheObject)Serializer.fromString(UserManager.JEDIS.get(key));
        } catch (Exception e) {
            System.out.println("Something bad happened: " + e);
            return Response.status(Response.Status.OK).entity(DBManager.FAILURE.toJson()).build();
        }
    
        if (!isArticlePresent(cacheObject.userName, article.getTitle())) {
            return Response.status(Response.Status.OK).entity(DBManager.ARTICLEEXISTS.toJson()).build();
        } 

        Document newDocument = new Document(ArticleResource.AUTHOR, cacheObject.userName)
                                        .append(ArticleResource.TITLE, article.getTitle())
                                        .append(ArticleResource.BODY, article.getBody())
                                        .append(ArticleResource.DATE, new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
        MongoClient client = DBManager.loginUser(cacheObject.userName, cacheObject.mongoPass);

        client.getDatabase(DBManager.DATABASENAME).getCollection(DBManager.ARTICLES).insertOne(newDocument);
        System.out.println("Finished creating article ... " + ArticleResource.class.getSimpleName() + " [60]");
        client.close();

        return Response.status(Response.Status.OK).entity(DBManager.SUCCESS.toJson()).build();
    }

    @POST
    @Path("/Delete")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteArticle(@CookieParam(UserManager.COOKIEOFTHEGODS) Cookie cookie, Article article) throws UnknownUriException {
        String key = UserManager.checkCache(cookie, article.getKey());

        if (key == null) {
            return Response.status(Response.Status.OK).entity(DBManager.NOTLOGGEDIN.toJson()).build();
        }

        if (UserManager.JEDIS.get(key) == null) {
            return Response.status(Response.Status.OK).entity(DBManager.NOTLOGGEDIN.toJson()).build();
        }

        System.out.println("Deleting Article ... " + ArticleResource.class.getSimpleName() + " [86]");
        UserManager.CacheObject cacheObject;

        try {
            cacheObject = (CacheObject)Serializer.fromString(UserManager.JEDIS.get(key));
        } catch (Exception e) {
            System.out.println("Something bad happened: " + e);
            return Response.status(Response.Status.OK).entity(DBManager.FAILURE.toJson()).build();
        }
        Document newDocument = new Document(ArticleResource.AUTHOR, cacheObject.userName)
                                        .append(ArticleResource.TITLE, article.getTitle())
                                        .append(ArticleResource.BODY, article.getBody());
        MongoClient client = DBManager.loginUser(cacheObject.userName, cacheObject.mongoPass);

        client.getDatabase(DBManager.DATABASENAME).getCollection(DBManager.ARTICLES).deleteOne(newDocument);
        System.out.println("Finished deleting article ... " + ArticleResource.class.getSimpleName() + " [87]");
        client.close();

        return Response.status(Response.Status.OK).entity(DBManager.SUCCESS.toJson()).build();
    }

    @POST
    @Path("/All")
    @Produces(MediaType.APPLICATION_JSON)
    @Traced(value = true, operationName = "Getting Articles")
    public Response getAllArticle(@CookieParam(UserManager.COOKIEOFTHEGODS) Cookie cookie, Article article) throws UnknownUriException {
        String key = UserManager.checkCache(cookie, article.getKey());

        if (key == null) {
            return Response.status(Response.Status.OK).entity(DBManager.NOTLOGGEDIN.toJson()).build();
        }

        if (UserManager.JEDIS.get(key) == null) {
            return Response.status(Response.Status.OK).entity(DBManager.NOTLOGGEDIN.toJson()).build();
        }

        System.out.println("Getting all articles ... " + ArticleResource.class.getSimpleName() + " [110]");
        UserManager.CacheObject cacheObject;

        try {
            cacheObject = (CacheObject)Serializer.fromString(UserManager.JEDIS.get(key));
        } catch (Exception e) {
            System.out.println("Something bad happened: " + e);
            return Response.status(Response.Status.OK).entity(DBManager.FAILURE.toJson()).build();
        }
        Document responseDoc = new Document();
        MongoClient client = DBManager.loginUser(cacheObject.userName, cacheObject.mongoPass);

        Iterator<Document> test = client.getDatabase(DBManager.DATABASENAME).getCollection(DBManager.ARTICLES).find().iterator();
        List<Document> docList = new ArrayList<>();
        
        while (test.hasNext()) {
            Document result = test.next();
            Document toAppend = new Document(AUTHOR, result.get(AUTHOR)).append(TITLE, result.get(TITLE)).append(BODY, result.get(BODY)).append(DATE, result.get(DATE));
            docList.add(toAppend);
        }

        responseDoc.append("articles", docList).append("msg", DBManager.SUCCESS.getString("msg"));
        System.out.println("Finished getting all articles ... " + ArticleResource.class.getSimpleName() + " [119]");
        client.close();

        return Response.status(Response.Status.OK).entity(responseDoc.toJson()).build();
    }

    private static boolean isArticlePresent(String userName, String title) {
        Iterator<Document> articles = DBManager.MONGO_ADMIN.getDatabase(DBManager.DATABASENAME).getCollection(DBManager.ARTICLES).find().iterator();

        while (articles.hasNext()) {
            Document result = articles.next();
            
            if (result.getString(AUTHOR).equals(userName) && result.getString(TITLE).equals(title)) {
                return false;
            }
        }
        return true;
    }
}