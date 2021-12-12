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
        String com = command.substring(0, sep);
        return commands.get(com).run(world, command.substring(sep+1));
    }
}
