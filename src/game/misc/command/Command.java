package game.misc.command;

import game.world.World;

import java.util.concurrent.Callable;

public abstract class Command {

    static Command basicCommand(String name, Callable<String>run){
        return new Command() {
            @Override
            public String run(World world, String arguments) {
                if(arguments.length() > 0){
                    return "Expected 0 arguments for basic command.";
                }
                try{
                    return run.call();
                } catch(Exception e){
                    return "error executing command \"" + name + "\": \n" + e.getMessage();
                }
            }

            @Override
            public String getCommand() {
                return name;
            }

            public boolean equals(Object o){
                if(o instanceof Command c){
                    return this.getCommand().equals(c.getCommand());
                } else {
                    return false;
                }
            }

            @Override
            public int hashCode() {
                return this.getCommand().hashCode();
            }
        };
    }

    static Command basicCommand(String name, Runnable run){
        return new Command() {
            @Override
            public String run(World world, String arguments) {
                if(arguments.length() > 0){
                    return "Expected 0 arguments for basic command.";
                }
                try{
                    run.run();
                    return "command \"" + name + "\" has been executed";
                } catch(Exception e){
                    return "error executing command \"" + name + "\": \n" + e.getMessage();
                }
            }

            @Override
            public String getCommand() {
                return name;
            }

            public boolean equals(Object o){
                if(o instanceof Command c){
                    return this.getCommand().equals(c.getCommand());
                } else {
                    return false;
                }
            }

            @Override
            public int hashCode() {
                return this.getCommand().hashCode();
            }
        };
    }
    /**
     * runs this command.
     * @param world the current World
     * @param arguments the text that comes after the command, excluding the space to separate command and arguments.
     * @return will be printed as result of running this function.
     */
    abstract String run(World world, String arguments);

    /**
     * returns the name of the command.
     * eg: "tp"
     * Used to determine which command should be executed given an input.
     * @return name of command
     */
    abstract String getCommand();

    /**
     * Don't override, unless you are returning the hash code of your command.
     * @return the hash code of the name of the command
     */
    public int hashCode(){
        return getCommand().hashCode();
    }
}
