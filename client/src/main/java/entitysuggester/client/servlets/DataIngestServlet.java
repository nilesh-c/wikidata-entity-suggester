/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entitysuggester.client.servlets;

import com.google.common.base.Charsets;
import java.io.*;
import java.nio.charset.Charset;
import java.util.NoSuchElementException;
import java.util.zip.GZIPInputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.mahout.cf.taste.common.TasteException;

/**
 *
 * @author nilesh
 */
public class DataIngestServlet extends AbstractEntitySuggesterServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        super.doPost(request, response);
        Reader reader;
        String contentEncoding = request.getHeader("Content-Encoding");
        if (contentEncoding == null) {
            reader = request.getReader();
        } else if ("gzip".equals(contentEncoding)) {
            String charEncodingName = request.getCharacterEncoding();
            Charset charEncoding = charEncodingName == null ? Charsets.UTF_8 : Charset.forName(charEncodingName);
            reader = new InputStreamReader(new GZIPInputStream(request.getInputStream()), charEncoding);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unsupported Content-Encoding");
            return;
        }

        BufferedReader br = new BufferedReader(reader);
        String a;
        StringBuilder sb = new StringBuilder();
        while ((a = br.readLine()) != null) {
            if (a.contains(",")) {
                sb.append(a).append("\n");
            }
        }

        try {
            getClientRecommender().ingest(new StringReader(sb.toString()));
        } catch (IllegalArgumentException iae) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, iae.toString());
        } catch (NoSuchElementException nsee) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, nsee.toString());
        } catch (TasteException te) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, te.toString());
            getServletContext().log("Unexpected error in " + getClass().getSimpleName(), te);
        }
    }
}
