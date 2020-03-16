package io.openliberty.guides.inventory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@RequestScoped
@Path("")
public class WebResource {

  @GET
  @Produces(MediaType.TEXT_HTML)
  public Response getCalculator() {
    
    return Response.status(Response.Status.OK)
        .entity(
          "<html>\n" +
          "<head>\n" +
          "<script>\n" +
          "function dis(val)\n" +
          "{ document.getElementById(\"result\").value+=val; }\n" +  
          "function solve() {\n" +
          "try { let x = document.getElementById(\"result\").value; \n" +
                    "let y = eval(x);\n" +
                    "document.getElementById(\"result\").value = y;}\ncatch(err)\n"+
                    "{ document.getElementById(\"result\").value = 'ERROR'; } }\n" +
                "function clr() { document.getElementById(\"result\").value = \"\"; }\n" +
          "</script>\n" +
             "<style>\n" +
             "html {\n" +
                  "font-size: 62.5%;\n" +
                  "box-sizing: border-box;\n}" +
                "*, *::before, *::after {\n" +
                  "margin: 0;\n" +
                  "padding: 0;\n" +
                  "box-sizing: inherit;}\n" +
                  "body {\n" + 
                    "margin:0; \n" + 
                    "padding:0; \n" +
                    "overflow:hidden; \n" +
                    "background-color:black; \n}\n" +
                    "section{\n" +
                    "height:100%;\n" +
                    "width:100%;\n" +
                      "position:absolute ;  background:radial-gradient(#333,#000);\n}\n" +
                    ".leaf{\n" +
                        "position:absolute ;\n" +
                        "width:100%;\n" +
                        "height:100%;\n" +
                        "top:0;\n" +
                        "left:0;\n}\n" +
                    ".leaf div{\n" +
                    "position:absolute ;\n" +
                    "display:block ;\n}\n" +
                    ".leaf div:nth-child(1){\n" +
                        "left:20%; \n" +
                        "animation:fall 15s linear infinite ;\n" +
                        "animation-delay:-2s;\n}\n" +
                    ".leaf div:nth-child(2){\n" +
                        "left:70%;\n" +
                        "animation:fall 15s linear infinite ;\n" +
                        "animation-delay:-4s;\n}\n" +
                    ".leaf div:nth-child(3){\n" +
                        "left:10%;\n" +
                        "animation:fall 20s linear infinite ;\n" +
                        "animation-delay:-7s; }\n" +
                    ".leaf div:nth-child(4){ \n" +
                        "left:50%; \n" +
                       "animation:fall 18s linear infinite ; \n" +
                       "animation-delay:-5s; }\n" +
                    ".leaf div:nth-child(5){\n" +
                        "left:85%;\n" + 
                        "animation:fall 14s linear infinite ;\n" +
                        "animation-delay:-5s; }\n" +
                    ".leaf div:nth-child(6){\n" +
                        "left:15%; \n" +
                        "animation:fall 16s linear infinite ;\n" +
                        "animation-delay:-10s; }\n" +
                    ".leaf div:nth-child(7){\n" +
                        "left:90%; \n" +
                        "animation:fall 15s linear infinite ;\n" +
                        "animation-delay:-4s; }\n" +
                    "@keyframes fall{\n" +
                        "0%{\n" +
                            "opacity:1;\n" +
                            "top:-10%;\n" +
                            "transform:translateX (20px) rotate(0deg);\n}\n" +
                        "20%{\n" +
                            "opacity:0.8;\n" +
                            "transform:translateX (-20px) rotate(45deg);\n}\n" +
                        "40%{\n" +
                            "transform:translateX (-20px) rotate(90deg);\n}\n" +
                        "60%{\n" +   
                           "transform:translateX (-20px) rotate(135deg);\n}\n" +
                        "80%{\n" +
                            "transform:translateX (-20px) rotate(180deg);\n}\n" +
                        "100%{\n" + 
                            "top:110%;\n" +
                            "transform:translateX (-20px) rotate(225deg);\n}\n}\n" +
                    ".leaf1{\n" +
                        "transform: rotateX(180deg);\n}\n" +
                    "h2{\n" +
                        "position:absolute ;\n" +
                        "top:40%;\n" +
                        "width:100%;\n" +
                        "font-family: 'Courgette', cursive;\n" +
                        "font-size:4em;\n" +
                        "text-align:center;\n" +
                        "transform:translate ;\n" +
                        "color:#fff;\n" +
                        "transform:translateY (-50%);\n}\n" +
                  ".calculator {\n" +
                    "border: 1px solid #ccc;\n" +
                    "border-radius: 5px;\n" +
                    "position: absolute;\n" +
                    "top: 50%;\n" +
                    "left: 50%;\n" +
                    "transform: translate(-50%, -50%);\n"+
                    "width: 400px;}\n" +
                    ".result {\n" +
                      "width: 100%;\n" +
                      "font-size: 5rem;\n" +
                      "height: 80px;\n" +
                      "border: none;\n" +
                      "background-color: #252525;\n" +
                      "color: #fff;\n" +
                      "text-align: right;\n" +
                      "padding-right: 20px;\n" +
                      "padding-left: 10px; }\n" +
                      "button { \n" +
                      "height: 60px;\n" +
                      "background-color: #fff;\n" +
                      "border-radius: 3px;\n" +
                      "border: 1px solid #c4c4c4;\n" +
                        "background-color: transparent;\n" +
                        "font-size: 2rem;\n" +
                        "color: #333;\n" +
                        "background-image: linear-gradient(to bottom,transparent,transparent 50%,rgba(0,0,0,.04));\n" +
                        "box-shadow: inset 0 0 0 1px rgba(255,255,255,.05), inset 0 1px 0 0 rgba(255,255,255,.45), inset 0 -1px 0 0 rgba(255,255,255,.15), 0 1px 0 0 rgba(255,255,255,.15);\n" +
                        "text-shadow: 0 1px rgba(255,255,255,.4); }\n" +
                      "button:hover {\n" +
                        "background-color: #eaeaea; }\n" +
                      ".operator {\n" +
                        "color: #337cac; }\n" +
                      ".all-clear {\n" +
                        "background-color: #f0595f;\n" +
                        "border-color: #b0353a;\n" +
                        "color: #fff; }\n" +
                      ".all-clear:hover {\n" +
                      "background-color: #f17377;}\n" +
                      ".equal-sign {\n" +
                        "background-color: #2e86c0;\n" +
                        "border-color: #337cac;\n" +
                        "color: #fff; }\n" +
                      ".equal-sign:hover {\n" +
                      "  background-color: #4e9ed4; }\n" +
                      ".calculator-keys { display: grid;\n" +
                      " grid-template-columns: repeat(4, 1fr);\n" +
                      "grid-gap: 20px;\n" +
                      "padding: 20px;}\n" +
                      ".equal-sign {\n" +
                        "background-color: #2e86c0;\n" +
                        "border-color: #337cac;\n" +
                        "color: #fff;\n" +
                        "height: 100%;\n" +
                        "grid-area: 2 / 4 / 6 / 5; }\n" +
                "</style> \n" +
          "</head> <body> \n" +
          "<div class=\"leaf\"><div><img src=\"http://www.pngmart.com/files/1/Fall-Autumn-Leaves-Transparent-PNG.png\" height=\"75px\" width=\"75px\"></img></div>\n" +
             "<div><img src=\"http://www.pngmart.com/files/1/Fall-Autumn-Leaves-Transparent-PNG.png\" height=\"75px\" width=\"75px\"></img></div>\n" +
             "<div><img src=\"http://www.pngmart.com/files/1/Transparent-Autumn-Leaves-Falling-PNG.png\" height=\"75px\" width=\"75px\"></img></div>\n" +
             "</div><div class=\"calculator\">\n" +
             "<input type=\"text\" id=\"result\" class=\"result\" value=\"\" />\n" +
             "<div class=\"calculator-keys\">\n" +
               "<button onclick=\"dis('+')\" type=\"button\" class=\"operator\" value=\"+\">+</button>\n" +
               "<button onclick=\"dis('-')\" type=\"button\" class=\"operator\" value=\"-\">-</button>\n" +
               "<button onclick=\"dis('*')\" type=\"button\" class=\"operator\" value=\"*\">&times;</button>\n" +
               "<button onclick=\"dis('/')\" type=\"button\" class=\"operator\" value=\"/\">&divide;</button>\n" +
               "<button onclick=\"dis('7')\" type=\"button\" value=\"7\">7</button>\n" +
               "<button onclick=\"dis('8')\" type=\"button\" value=\"8\">8</button>\n" +
               "<button onclick=\"dis('9')\" type=\"button\" value=\"9\">9</button>\n" +
               "<button onclick=\"dis('4')\" type=\"button\" value=\"4\">4</button>\n" +
               "<button onclick=\"dis('5')\" type=\"button\" value=\"5\">5</button>\n" +
               "<button onclick=\"dis('6')\" type=\"button\" value=\"6\">6</button>\n" +
               "<button onclick=\"dis('1')\" type=\"button\" value=\"1\">1</button>\n" +
               "<button onclick=\"dis('2')\" type=\"button\" value=\"2\">2</button>\n" +
               "<button onclick=\"dis('3')\" type=\"button\" value=\"3\">3</button>\n" +
               "<button onclick=\"dis('0')\" type=\"button\" value=\"0\">0</button>\n" +
               "<button onclick=\"dis('.')\" type=\"button\" class=\"decimal\" value=\".\">.</button>\n" +
               "<button onclick=\"clr()\" type=\"button\" class=\"all-clear\" value=\"all-clear\">AC</button>\n" +
               "<button onclick=\"solve()\" type=\"button\" class=\"equal-sign\" value=\"=\">=</button>\n" +
             "</div>\n" +
           "</div>\n" +
           "</body> </html>\n"
        ).build();
  }
}
