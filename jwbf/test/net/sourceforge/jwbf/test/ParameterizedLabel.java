package net.sourceforge.jwbf.test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.Parameterized;

public class ParameterizedLabel extends Parameterized {

  private NameFinder namer;

  public ParameterizedLabel(Class<?> klass) throws Throwable {
    super(klass);
    Class<?> testName;
    if (klass.isAnnotationPresent(TestNamer.class)) {
      testName = klass.getAnnotation(TestNamer.class).value();

      Object obj = testName.newInstance();
      List<Class<?>> interfaces = Arrays.asList(obj.getClass().getInterfaces());
      if (interfaces.size() > 0 && interfaces.contains(NameFinder.class)) {
        namer = (NameFinder) obj;
      }
    }
  }

  @Override
  protected Description describeChild(final Runner child) {
    Runner clone = new Runner() {

      @Override
      public void run(RunNotifier notifier) {
        child.run(notifier);
      }

      @Override
      public Description getDescription() {
        Description mod = child.getDescription();
        if (namer != null) {
          try {
            Field paramList = child.getClass().getDeclaredField("fParameterList");
            paramList.setAccessible(true);
            List<?> lis = (List<?>) paramList.get(child);

            Field firstNameField = mod.getClass().getDeclaredField("fDisplayName");
            firstNameField.setAccessible(true);
            String posStr = (String) firstNameField.get(mod);
            posStr = posStr.replaceAll("[\\[\\]]", "");
            String result = namer.getName(lis, Integer.parseInt(posStr));
            if (result != null)
              firstNameField.set(mod, result);
          } catch (NoSuchFieldException e) {
            e.printStackTrace();
          } catch (IllegalAccessException e) {
            e.printStackTrace();
          }
        }
        return mod;
      }
    };
    return super.describeChild(clone);
  }

}

