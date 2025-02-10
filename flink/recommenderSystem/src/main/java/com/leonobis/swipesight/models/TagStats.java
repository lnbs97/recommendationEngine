package com.leonobis.swipesight.models;

/**
 * This class is used to keep track of the likes and dislikes for attraction tags.
 */
public class TagStats {
    private int likes;
    private int dislikes;

    public TagStats() {
        this.likes = 0;
        this.dislikes = 0;
    }

    public void incrementLikes() {
        likes++;
    }

    public void incrementDislikes() {
        dislikes++;
    }

    public double getLikeProbability() {
        int total = likes + dislikes;
        return total > 0 ? (double) likes / total : 0.0;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    @Override
    public String toString() {
        return "TagStats{" +
                "likes=" + likes +
                ", dislikes=" + dislikes +
                ", likeProbability=" + getLikeProbability() +
                '}';
    }
}
