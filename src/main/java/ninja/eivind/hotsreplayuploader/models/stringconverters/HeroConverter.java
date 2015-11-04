package ninja.eivind.hotsreplayuploader.models.stringconverters;

import javafx.util.StringConverter;
import ninja.eivind.hotsreplayuploader.models.Hero;

public class HeroConverter extends StringConverter<Hero> {

    @Override
    public String toString(final Hero hero) {
        if (hero == null) {
            return "";
        }
        return hero.getPrimaryName();
    }

    @Override
    public Hero fromString(final String primaryName) {
        final String imageURL = primaryName.replaceAll("[\\W_]", "");
        if (imageURL.equals("")) {
            return null;
        }

        return new Hero(primaryName, imageURL);
    }
}
