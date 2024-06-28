package com.gustlikplugins.AutoNightmareZone;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public abstract class State {

    private HashMap<Class, State> nextStates = new HashMap<Class, State>();

    public abstract String getName();

    protected AutoNightmareZonePlugin plugin;

    public final State initialState;

    public State(AutoNightmareZonePlugin plugin){
        this.plugin = plugin;
        this.initialState = this;
    }


    public State(AutoNightmareZonePlugin plugin, State initialState){
        this.plugin = plugin;
        this.initialState = initialState;
        nextStates.put(initialState.getClass(), initialState);
    }

    public abstract State getState();
    public abstract void onGameTick();



    protected State getState(Class stateClass){
        return getStateNonrecursive(stateClass).getState();
    }

    protected State getStateNonrecursive(Class stateClass){
        if(stateClass.isAssignableFrom(State.class))
            throw new RuntimeException("Invalid state classes passed to getState");

        if(stateClass == this.getClass())
            return this;

        if(!nextStates.containsKey(stateClass)) {
            Constructor<State> constructor = null;
            State nextState = null;


            try {
                constructor = stateClass.getConstructor(AutoNightmareZonePlugin.class, State.class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }

            try {
                nextState = constructor.newInstance(plugin, initialState);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }


            nextStates.put(stateClass, nextState);
        }
        return nextStates.get(stateClass); //Forgetting this recursion call is not a fun time :(
    }


}
