package deathrunebuyer.buytasks;

import deathrunebuyer.wDeathRuneBuyer;
import net.runelite.api.coords.WorldPoint;
import simple.hooks.filters.SimpleShop.Amount;
import simple.hooks.scripts.task.Task;
import simple.hooks.wrappers.SimpleItem;
import simple.hooks.wrappers.SimpleNpc;
import simple.robot.api.ClientContext;
import simple.robot.utils.WorldArea;

public class BuyIsle extends Task {

	WorldArea shop = new WorldArea(new WorldPoint(2445, 4653, 0), new WorldPoint(2457, 4642, 0));

	private wDeathRuneBuyer main;

	public BuyIsle(ClientContext ctx, wDeathRuneBuyer main) {
		super(ctx);
		this.main = main;
	}

	@Override
	public boolean condition() {
		return ctx.pathing.inArea(shop) && main.buyMageArea == false && main.buyIsle == true;
	}

	@Override
	public void run() {
		/**
		 * Paint purposes
		 */
		int invCount = ctx.inventory.populate().filter("Death rune").population(true);
		if(invCount > main.prevInvCount) main.runesBought += (invCount - main.prevInvCount);
		main.prevInvCount = invCount;
		
		SimpleNpc yaga = ctx.npcs.populate().filter("Baba Yaga").nearest().next();
		if (yaga != null && yaga.validateInteractable() && !ctx.shop.shopOpen()) {
			yaga.click("Trade");
			ctx.onCondition(() -> ctx.shop.shopOpen(), 2000);
		}
		if (ctx.shop.shopOpen()) {
			int countGP = ctx.inventory.populate().filter("Coins").population(true);
			SimpleItem runes = ctx.shop.populate().filter("Death rune").next();
			if (runes != null && ctx.shop.populate().filter("Death rune").population(true) > 0 && countGP >= 180) {
				ctx.shop.buy("Death rune", Amount.FIFTY);
			}
			if (ctx.shop.populate().filter("Death rune").population(true) <= 0 || countGP < 180) {
				ctx.updateStatus("Teleporting home");
				ctx.shop.closeShop();
				ctx.magic.castSpellOnce("Zenyte Home Teleport");
				main.buyMageArea = true;
				main.buyIsle = false;
				ctx.onCondition(() -> !ctx.pathing.inArea(shop), 2000);
			}
		}
		int teleAnims[] = { 4847, 4850, 4853, 4855, 4857 };
		for (int i : teleAnims) {
			if (ctx.players.getLocal().getAnimation() == i) {
				return;
			}
		}
	}

	@Override
	public String status() {
		return "Buying death runes";
	}

}
