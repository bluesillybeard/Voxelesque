package game.misc.command;

import game.world.World;

import java.util.HashMap;

public class Commands {
    private final HashMap<String, Command> commands; //command name -> command.
    public Commands() {
        this.commands = new HashMap<>();
    }

    public boolean add(Command c){
        if(commands.containsKey(c.getCommand())){
            return false;
        } else {
            commands.put(c.getCommand(), c);
            return true;
        }
    }

    public String runCommand(World world, String command){
        int sep = command.indexOf(' ');
        if(sep != -1) {
            String com = command.substring(0, sep);
            Command c = commands.get(com);
            if(c!=null)return c.run(world, command.substring(sep + 1));
        } else {
            Command c = commands.get(command);
            if(c!=null)return c.run(world, "");
        }
        return "command \"" + command + "\" does not exist.";
    }
}
