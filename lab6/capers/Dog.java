package capers;

import javax.management.ObjectName;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static capers.CapersRepository.CAPERS_FOLDER;
import static capers.CapersRepository.CWD;import static capers.Utils.*;

/** Represents a dog that can be serialized.
 * @author TODO
*/
public class Dog implements Serializable{ // TODO

    /** Folder that dogs live in. */
    static final File DOG_FOLDER = Utils.join(CAPERS_FOLDER, "dogs"); // TODO (hint: look at the `join`
                                         //      function in Utils)

    /** Age of dog. */
    private int age;
    /** Breed of dog. */
    private String breed;
    /** Name of dog. */
    private String name;

    /**
     * Creates a dog object with the specified parameters.
     * @param name Name of dog
     * @param breed Breed of dog
     * @param age Age of dog
     */
    public Dog(String name, String breed, int age) {
        this.age = age;
        this.breed = breed;
        this.name = name;
    }

    /**
     * Reads in and deserializes a dog from a file with name NAME in DOG_FOLDER.
     *
     * @param name Name of dog to load
     * @return Dog read from file
     */
    public static Dog fromFile(String name) {
        File file = Utils.join(DOG_FOLDER, name + ".txt");
        // TODO (hint: look at the Utils file)
        return Utils.readObject(file, Dog.class);
    }

    /**
     * Increases a dog's age and celebrates!
     */
    public void haveBirthday() {
        age += 1;
        System.out.println(toString());
        System.out.print("Happy birthday! Woof! Woof!");
    }

    /**
     * Saves a dog to a file for future use.
     */
    public void saveDog() throws IOException {
        // TODO (hint: don't forget dog names are unique)
        Path Dir = Paths.get(DOG_FOLDER.toURI());
        Set<String> names = new HashSet<>();
        try(Stream<Path> paths = Files.walk(Dir)) {
            names = paths
                    .map(Path::getFileName)
                    .map(Object::toString)
                    .collect(Collectors.toSet());
        }
        if(!names.contains(name + ".txt")){
            File NameSet = Utils.join(DOG_FOLDER, name + ".txt");
            Dog dog = new Dog(name, breed, age);
            Utils.writeObject(NameSet, dog);
            System.out.println(dog);
            NameSet.createNewFile();
        }
    }

    @Override
    public String toString() {
        return String.format(
            "Woof! My name is %s and I am a %s! I am %d years old! Woof!",
            name, breed, age);
    }

}
