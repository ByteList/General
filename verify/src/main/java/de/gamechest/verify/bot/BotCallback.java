package de.gamechest.verify.bot;

/**
 * Created by ByteList on 24.02.2018.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public interface BotCallback<T> {

    void run(T result);
}
