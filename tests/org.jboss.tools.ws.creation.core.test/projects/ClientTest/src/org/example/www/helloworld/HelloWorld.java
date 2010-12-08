
package org.example.www.helloworld;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.1-b03-
 * Generated source version: 2.0
 * 
 */
@WebService(name = "HelloWorld", targetNamespace = "http://www.example.org/HelloWorld")
public interface HelloWorld {


    /**
     * 
     * @param sayHelloRequest
     * @return
     *     returns java.lang.String
     */
    @WebMethod(action = "http://www.example.org/HelloWorld/sayHello")
    @WebResult(name = "sayHelloResponse", targetNamespace = "")
    @RequestWrapper(localName = "sayHello", targetNamespace = "http://www.example.org/HelloWorld", className = "org.example.www.helloworld.SayHello")
    @ResponseWrapper(localName = "sayHelloResponse", targetNamespace = "http://www.example.org/HelloWorld", className = "org.example.www.helloworld.SayHelloResponse")
    public String sayHello(
        @WebParam(name = "sayHelloRequest", targetNamespace = "")
        String sayHelloRequest);

    /**
     * 
     * @param sayHelloRequest2
     * @return
     *     returns java.lang.String
     */
    @WebMethod(action = "http://www.example.org/HelloWorld/sayHello2")
    @WebResult(name = "sayHelloResponse2", targetNamespace = "")
    @RequestWrapper(localName = "sayHello2", targetNamespace = "http://www.example.org/HelloWorld", className = "org.example.www.helloworld.SayHello2")
    @ResponseWrapper(localName = "sayHello2Response", targetNamespace = "http://www.example.org/HelloWorld", className = "org.example.www.helloworld.SayHello2Response")
    public String sayHello2(
        @WebParam(name = "sayHelloRequest2", targetNamespace = "")
        String sayHelloRequest2);

}
