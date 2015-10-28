package pl.gajewski.server.chat.operations;

import org.json.JSONObject;
import pl.gajewski.server.chat.user.UserHandler;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Gajo
 *         06/05/2015
 */

public class OperationFactory {

    private static Class<?>[] constructorParams = new Class[]{
            UserHandler.class,
            JSONObject.class
    };

    public static IOperation create(UserHandler userHandler, JSONObject jsonObject) throws ClassNotFoundException,
            NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        String type = (String) jsonObject.get("type");
        String classPath = "pl.gajewski.server.chat.operations.types." + OperationType.valueOf(type).getClassName();

        Class<?> aClass = Class.forName(classPath);

        // create new instance
        Constructor<?> constructor = aClass.getConstructor(constructorParams);
        return (IOperation) constructor.newInstance(userHandler, jsonObject);
    }

}
