package hu.zoldleo.dragonborn.util;

import net.minecraft.world.entity.player.Player;

public class ExperienceUtils {

    public static int getExperienceForLevelAfter(int level) {
        if (level >= 30)
            return 112 + (level - 30) * 9;
        return level >= 15 ? 37 + (level - 15) * 5 : 7 + level * 2;
    }

    public static int getLevel(int experience) {
        int totalExperienceFor16 = 352;
        int totalExperienceFor31 = 1507;
        if (experience <= totalExperienceFor16)
            return (int) (Math.sqrt(experience + 9) - 3);
        if (experience <= totalExperienceFor31)
            return (int) (Math.sqrt((2.f / 5.f) * (experience - 7839.f / 40.f)) + 81.f / 10.f);
        return (int) (Math.sqrt((2.f / 9.f) * (experience - 54215.f / 72.f)) + 325.f / 18.f);
    }

    public static double getLevelAndProgress(int experience) {
        int wholeLevel = getLevel(experience);

        int requiredForNext = getExperienceForLevelAfter(wholeLevel + 1);
        double progress = (double) (experience - getTotalExperience(wholeLevel)) / requiredForNext;
        return wholeLevel + progress;
    }

    public static int getTotalExperience(int targetLevel) {
        if (targetLevel <= 16)
            return (targetLevel * targetLevel + (6 * targetLevel));
        if (targetLevel <= 31)
            return (int) (2.5 * targetLevel * targetLevel - (40.5 * targetLevel) + 360);
        return (int) (4.5 * targetLevel * targetLevel - (162.5 * targetLevel) + 2220);
    }

    public static int getTotalExperience(final Player player) {
        int currentExperience = getTotalExperience(player.experienceLevel);
        return (int) (currentExperience + player.experienceProgress * getExperienceForLevelAfter(player.experienceLevel));
    }
}