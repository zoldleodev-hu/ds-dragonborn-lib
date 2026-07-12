package hu.zoldleo.dragonborn.api.dragon_type;

import java.util.List;

/**
 * Provides a custom list of dragon bodies to use. If not applied, the default list will be used.
 * <p>
 * Usable on: dragon type
 */
public interface IBodyListProvider {
    /**
     * Should return at least one body.
     */
    List<String> getBodies();
}