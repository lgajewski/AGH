package lgajewski.distributed.lab3;

import java.util.Random;

public enum Task {

    SUM("sum", "+"),
    SUBTRACT("subtract", "-"),
    MULTIPLY("multiply", "*"),
    DIVIDE("divide", "/"),
    POW("pow", "^"),
    DIV("div", "div"),
    MOD("mod", "%");

    private final String taskName;
    private final String operator;

    Task(String taskName, String operator) {
        this.taskName = taskName;
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }

    public String getTopicName() {
        return "t-" + taskName;
    }

    public String getQueueName() {
        return "q-task";
    }

    public static class RandomTask<E extends Enum> {
        private static final Random RND = new Random();
        private final E[] values;

        public RandomTask(Class<E> token) {
            values = token.getEnumConstants();
        }

        public E random() {
            return values[RND.nextInt(values.length)];
        }
    }
}
