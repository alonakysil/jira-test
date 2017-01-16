package kysil.alona.jira.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;

public class RESTClient {

    private final String proto = "https";
    private String login;
    private String password;
    private String baseURL;
    private String cookie;
    
    public RESTClient(String baseURL, String login, String password) {
        super();
        this.setBaseURL(baseURL);
        this.setLogin(login);
        this.setPassword(password);
        this.setCookie(this.login());
    }

    private String getCookie() {
        return cookie;
    }

    private void setCookie(String cookie) {
        this.cookie = cookie;
    }

    private String getBaseURL() {
        return baseURL;
    }

    private void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }
    
    
    
    private void setLogin(String login) {
        this.login = login;
    }

    private void setPassword(String password) {
        this.password = password;
    }

    private String getLogin() {
        return login;
    }

    private String getPassword() {
        return password;
    }

    private String login() {
        String cookie = "";
        URLConnection urlConnection;
        try {
            URL jiraREST_URL = new URL(this.proto, this.getBaseURL(),"/rest/auth/1/session" );//new URL("https://alonatest.atlassian.net/rest/api/2/search");
            urlConnection = jiraREST_URL.openConnection();
            urlConnection.setDoInput(true);
            
            HttpURLConnection conn = (HttpURLConnection) jiraREST_URL.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            
            String data = this.buildAuthInfo();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Content-Length", String.valueOf(data.length()));
            conn.getOutputStream().write(data.getBytes());
            
            try {
                int status = conn.getResponseCode();
                InputStream inputStream;
                List<HttpCookie> cookies = HttpCookie.parse(conn.getHeaderField("Set-Cookie"));
                if (status == 400) {
                    inputStream = conn.getErrorStream();
                } else {
                    inputStream = conn.getInputStream();
                }
                BufferedReader br = null;
                StringBuilder sb = new StringBuilder();

                String line;
                try {

                    br = new BufferedReader(new InputStreamReader(inputStream));
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (br != null) {
                        try {
                            br.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                JsonReader jsonReader = Json.createReader(new StringReader(sb.toString()));
                JsonObject object = jsonReader.readObject();
                jsonReader.close();
                HttpCookie sessionCookie = null;
                for (HttpCookie httpCookie : cookies) {
                    if(httpCookie.getName().equals(((JsonObject)object.get("session")).getString("name"))) {
                        sessionCookie = httpCookie;
                        break;
                    }
                }
                cookie = sessionCookie.getName() + "=" + sessionCookie.getValue();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return cookie;
    }
    
    private String buildAuthInfo(){
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("username", this.getLogin());
        builder.add("password", this.getPassword());
        return    builder.build().toString();
    }
    
    public String sendRequest(String relURL, String type, String data) {
        String result = "";
        URLConnection urlConnection;
        try {
            URL jiraREST_URL = new URL(this.proto, this.getBaseURL(),relURL );//new URL("https://alonatest.atlassian.net/rest/api/2/search");
            urlConnection = jiraREST_URL.openConnection();
            urlConnection.setDoInput(true);
            
            HttpURLConnection conn = (HttpURLConnection) jiraREST_URL.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);

            conn.setRequestMethod(type);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("cookie", this.getCookie());
            if ( null != data ) {
                conn.setRequestProperty("Content-Length", String.valueOf(data.length()));
                conn.getOutputStream().write(data.getBytes());
            }
            
            try {
                int status = conn.getResponseCode();
                InputStream inputStream;
                if (status == 400) {
                    inputStream = conn.getErrorStream();
                }
                else if (status == 401) {
                    this.login();
                    return this.sendRequest(relURL, type, data);
                } else {
                    inputStream = conn.getInputStream();
                }
                BufferedReader br = null;
                StringBuilder sb = new StringBuilder();

                String line;
                try {

                    br = new BufferedReader(new InputStreamReader(inputStream));
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (br != null) {
                        try {
                            br.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                result = sb.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    
}
