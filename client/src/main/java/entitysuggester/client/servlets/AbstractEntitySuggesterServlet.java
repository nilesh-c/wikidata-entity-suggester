/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entitysuggester.client.servlets;

import com.google.common.base.Splitter;
import entitysuggester.client.recommender.WebClientRecommender;
import java.io.IOException;
import java.net.URL;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.myrrix.client.MyrrixClientConfiguration;
import net.myrrix.web.servlets.AbstractMyrrixServlet;

/**
 *
 * @author nilesh
 */
public abstract class AbstractEntitySuggesterServlet extends AbstractMyrrixServlet {

    static final Splitter SLASH = Splitter.on('/').omitEmptyStrings();
    private WebClientRecommender recommender;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        MyrrixClientConfiguration config = new MyrrixClientConfiguration();
        config.setHost(request.getServerName());
        config.setPort(request.getServerPort());
        recommender = new WebClientRecommender(config);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        MyrrixClientConfiguration config = new MyrrixClientConfiguration();
        config.setHost(request.getServerName());
        config.setPort(request.getServerPort());
        recommender = new WebClientRecommender(config);
    }

    protected final WebClientRecommender getClientRecommender() {
        return recommender;
    }

    protected final URL getPropFilePath(String name) throws NamingException {
        Context initCtx = new InitialContext();
        Context envCtx = (Context) initCtx.lookup("java:comp/env");
        String fileName = (String) envCtx.lookup(name);
        URL filePath = getClass().getClassLoader().getResource(fileName);
        return filePath;
    }
}
