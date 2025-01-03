package app;

public class Persona {
    public String name;
    public String imageURL;
    public int age;
    public String background;
    public String needsAndGoals;
    public String skillAndExperience;
    public String directQuotes;

    public Persona(String name, String imageURL, int age, String background, String needsAndGoals, 
                   String skillAndExperience, String directQuotes) {
        this.name = name;
        this.imageURL = "https://drive.google.com/uc?export=view&id=" + imageURL.split("/file/d/")[1].split("/")[0];
        this.age = age;
        this.background = background;
        this.needsAndGoals = needsAndGoals;
        this.skillAndExperience = skillAndExperience;
        this.directQuotes = directQuotes;
    } 

    public String getImageSource() {
        return ("persona_" + name.split(" ")[0] + name.split(" ")[1]+ ".png");
    }
}
