package capers;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static capers.Dog.DOG_FOLDER;
import static capers.Dog.fromFile;
import static capers.Utils.*;

/** A repository for Capers 
 * @author TODO
 * The structure of a Capers Repository is as follows:
 *
 * .capers/ -- top level folder for all persistent data in your lab12 folder
 *    - dogs/ -- folder containing all of the persistent data for dogs
 *    - story -- file containing the current story
 *
 * TODO: change the above structure if you do something different.
 *
 */
public class CapersRepository {
    /** Current Working Directory. */
    static final File CWD = new File(System.getProperty("user.dir"));

    /** Main metadata folder. */
    static final File CAPERS_FOLDER = Utils.join(CWD,"\\capers\\lab12"); // TODO Hint: look at the `join`
                                            //      function in Utils

    /**
     * Does required filesystem operations to allow for persistence.
     * (creates any necessary folders or files)
     * Remember: recommended structure (you do not have to follow):
     *
     * .capers/ -- top level folder for all persistent data in your lab12 folder
     *    - dogs/ -- folder containing all of the persistent data for dogs
     *    - story -- file containing the current story
     */
    public static void setupPersistence() throws IOException {
        // TODO
        boolean mkdir = CAPERS_FOLDER.mkdir();
        if(mkdir){
            boolean mkdir1 = DOG_FOLDER.mkdir();
            File story = Utils.join(CAPERS_FOLDER, "story.txt");
            if(! story.exists()){
                boolean newFile = story.createNewFile();
            }
            File dogExist = Utils.join(DOG_FOLDER, "\\dogs");
            if(! dogExist.exists()){
                boolean newDog = dogExist.createNewFile();
            }
        }
    }

    /**
     * Appends the first non-command argument in args
     * to a file called `story` in the .capers directory.
     * @param text String of the text to be appended to the story
     */
    public static void writeStory(String text) {
        // TODO
        File story = Utils.join(CAPERS_FOLDER, "story.txt");
        StringBuilder temp = new StringBuilder(readContentsAsString(story));
        if(story.length() != 0){
            System.out.println(temp);
            temp.append("\n");
        }
        temp.append(text);
        System.out.println(text);
        Utils.writeContents(story, temp.toString());
    }

    /**
     * Creates and persistently saves a dog using the first
     * three non-command arguments of args (name, breed, age).
     * Also prints out the dog's information using toString().
     */
    public static void makeDog(String name, String breed, int age) throws IOException {
        // TODO
        Dog dog = new Dog(name, breed, age);
        dog.saveDog();
    }

    /**
     * Advances a dog's age persistently and prints out a celebratory message.
     * Also prints out the dog's information using toString().
     * Chooses dog to advance based on the first non-command argument of args.
     * @param name String name of the Dog whose birthday we're celebrating.
     */
    public static void celebrateBirthday(String name) throws IOException {
        // TODO
       File file = Utils.join(DOG_FOLDER, name + ".txt");
        Dog dog = fromFile(name);
        dog.haveBirthday();
        Utils.writeObject(file, dog);
    }
}
