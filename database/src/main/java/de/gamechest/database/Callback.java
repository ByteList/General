package de.gamechest.database;

/**
 * Created by ByteList on 05.07.2017.
 * <p>
 * Copyright by ByteList - https://bytelist.de/
 */
public interface Callback<T> {

    void run(T result);
}
