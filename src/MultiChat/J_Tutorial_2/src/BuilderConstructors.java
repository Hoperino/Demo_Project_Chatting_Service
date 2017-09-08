import java.awt.*;

/**
 * Created by Mihail Milkov
 * This class represents two ways of building a constructor for an object
 * The context is Nutrition facts. They have often long list of variables that have to be added - some are required
 * some are optional. How can we build a constructor that initializes such object?
 *
 * First we show how it is done conventionally, then
 * We will show how it is done via Builder technique
 */
public class BuilderConstructors {
    public static void main(String[] args){
        NutritionFactsBuilder cocaCola = new NutritionFactsBuilder.Builder(240, 8).
                calories(100).sodium(35).carbohydrate(27).build();
    }

    public class NutritionFacts{
        private final int servingSize; // (mL) required
        private final int servings; // (per container) required

        private final int calories; // optional
        private final int fat; // (g) optional
        private final int sodium; // (mg) optional
        private final int carbohydrate; // (g) optional

        //List all possible constructors starting from the full-param constructor

        public NutritionFacts(int servingSize, int servings, int calories, int fat, int sodium, int carbohydrate){
            this.servingSize = servingSize;
            this.servings = servings;
            this.calories = calories;
            this.fat = fat;
            this.sodium = sodium;
            this.carbohydrate = carbohydrate;
        }

        public NutritionFacts(int servingSize, int servings, int calories, int fat, int sodium){
            this(servingSize,servings,calories,fat,sodium,0);
        }

        public NutritionFacts(int servingSize, int servings, int calories, int fat){
            this(servingSize, servings, calories, fat, 0);
        }

        public NutritionFacts(int servingSize, int servings, int calories){
            this(servingSize, servings, calories,0);
        }

        public NutritionFacts(int servingSize, int servings){
            this(servingSize, servings,0);
        }

        /*
         * This concludes the Conventional way of doing our constructors
         */

        //Using builder

    }

    public static class NutritionFactsBuilder{
        private final int servingSize; // (mL) required    Initi with -1 since they are required
        private final int servings;   // (per container) required

        private final int calories ; // optional
        private final int fat; // (g) optional
        private final int sodium; // (mg) optional
        private final int carbohydrate; // (g) optional

        public static class Builder{

            // Required parameters
            private final int servingSize;
            private final int servings;
            // Optional parameters - initialized to default values
            private int calories = 0;
            private int fat = 0;
            private int carbohydrate = 0;
            private int sodium = 0;

            public Builder(int servingSizeValue, int servingsValue){
                servingSize = servingSizeValue;
                servings = servingsValue;
            }

            public Builder calories(int value){
                calories = value;
                return this;
            }

            public Builder fat(int value){
                fat = value;
                return this;
            }

            public Builder sodium(int value){
                sodium = value;
                return this;
            }

            public Builder carbohydrate(int value){
                carbohydrate = value;
                return this;
            }

            public NutritionFactsBuilder build(){
                return new NutritionFactsBuilder(this);
            }

        }

        private NutritionFactsBuilder(Builder builder) {
            servingSize = builder.servingSize;
            servings = builder.servings;
            calories = builder.calories;
            fat = builder.fat;
            sodium = builder.sodium;
            carbohydrate = builder.carbohydrate;
        }
    }
}
