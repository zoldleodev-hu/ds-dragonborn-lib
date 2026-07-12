package hu.zoldleo.dragonborn.api.dragon_type;

import java.util.List;

/**
 * Provies access to the diet config. Not applying this interface results in a crash.
 * <p>
 * Usable on: dragon type
 */
public interface IDietProvider {
    List<String> getDietConfig();

    default boolean canEatHumanFood() {
        return false;
    }
}