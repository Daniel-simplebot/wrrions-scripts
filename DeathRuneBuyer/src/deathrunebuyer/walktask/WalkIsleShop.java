package deathrunebuyer.walktask;

import net.runelite.api.coords.WorldPoint;
import simple.hooks.scripts.task.Task;
import simple.hooks.wrappers.SimpleNpc;
import simple.robot.api.ClientContext;
import simple.robot.utils.WorldArea;

public class WalkIsleShop extends Task {

	WorldArea isle = new WorldArea(new WorldPoint(2060, 3934, 0), new WorldPoint(2113, 3887, 0));
	
	public WalkIsleShop(ClientContext ctx) {
		super(ctx);
	}

	@Override
	public boolean condition() {
		return ctx.pathing.inArea(isle);
	}
	
	WorldPoint[] path = {
		    new WorldPoint(2107, 3917, 0),
		    new WorldPoint(2106, 3922, 0),
		    new WorldPoint(2101, 3926, 0),
		    new WorldPoint(2095, 3930, 0),
		    new WorldPoint(2089, 3931, 0),
		    new WorldPoint(2082, 3931, 0)
		};

	@Override
	public void run() {
		SimpleNpc house = ctx.npcs.populate().filter("House").nearest().next();
		if (house == null) {
			ctx.pathing.walkPath(path);
		}
		if (house != null && house.validateInteractable()) {
			house.click("Go-inside");
			ctx.onCondition(() -> !ctx.pathing.inArea(isle), 3000);
		}
	}

	@Override
	public String status() {
		return "Walking to shop";
	}

}
