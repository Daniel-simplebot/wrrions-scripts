package deathrunebuyer.banktask;

import java.awt.Point;

import deathrunebuyer.wDeathRuneBuyer;
import net.runelite.api.coords.WorldPoint;
import simple.hooks.scripts.task.Task;
import simple.hooks.wrappers.SimpleObject;
import simple.hooks.wrappers.SimpleWidget;
import simple.robot.api.ClientContext;
import simple.robot.utils.WorldArea;

public class BankTask extends Task {

	WorldArea home = new WorldArea(new WorldPoint(3051, 3522, 0), new WorldPoint(3132, 3439, 0));

	private wDeathRuneBuyer main;
	
	public BankTask(ClientContext ctx, wDeathRuneBuyer main) {
		super(ctx);
		this.main = main;
	}

	@Override
	public boolean condition() {
		return ctx.pathing.inArea(home);
	}

	@Override
	public void run() {
		int invCount = ctx.inventory.populate().filter("Death rune").population(true);
		if(invCount > main.prevInvCount) main.runesBought += (invCount - main.prevInvCount);
		main.prevInvCount = invCount;
		
		int countGP = ctx.inventory.populate().filter("Coins").population(true);
		SimpleObject bank = ctx.objects.populate().filter("Grand Exchange booth").filterHasAction("Bank").nearest().next();
		SimpleObject portal = ctx.objects.populate().filter("Zenyte Portal").nearest().next();
		if (countGP < 180) {
			ctx.updateStatus("Finding bank, need GP");
			if (bank != null && bank.validateInteractable()) {
				bank.click("Bank");
				ctx.onCondition(() -> ctx.bank.bankOpen(), 3000);
			}
		}
		if (ctx.bank.bankOpen() && ctx.bank.populate().filter("Coins").population(true) >= 180) {
			ctx.updateStatus("Withdrawing coins");
			ctx.bank.depositInventory();
			ctx.bank.withdraw("Coins", main.amountCoins);
			ctx.bank.closeBank();
		} 
		if (ctx.bank.bankOpen() && ctx.bank.populate().filter("Coins").population(true) < 180) {
			ctx.updateStatus("Out of coins!");
			ctx.bank.closeBank();
			ctx.stopScript();
		}
		if (countGP >= 180) {
			if (!ctx.portalTeleports.zenytePortalOpen()) {
				if (portal != null && portal.visibleOnScreen() && !ctx.portalTeleports.zenytePortalOpen()) {
					portal.click("Teleport");
					ctx.onCondition(() -> ctx.portalTeleports.zenytePortalOpen(), 3000);
					ctx.updateStatus("Finding portal");
				}
				if (portal != null && !portal.visibleOnScreen() && !ctx.portalTeleports.zenytePortalOpen()) {
					ctx.pathing.step(new WorldPoint(3097, 3500, 0));
					portal.turnTo();
				}
			} 
			if (ctx.portalTeleports.zenytePortalOpen() && main.buyIsle == true) {
				ctx.sleep(1000);
				ctx.updateStatus("Teleporting to lunar isle");
				SimpleWidget misc = ctx.widgets.getWidget(700, 4);
				SimpleWidget lunarIsle = ctx.widgets.getWidget(700, 11);
				ctx.mouse.click(new Point(135, 226));
				ctx.sleep(1000);
				if (misc != null) {
					SimpleWidget button = misc.getDynamicChildren()[14];
					if (button.click(0)) {
						ctx.onCondition(() -> !ctx.pathing.inArea(home), 3000);
					}
				}
				if (lunarIsle != null) {
					SimpleWidget button = lunarIsle.getDynamicChildren()[16];
					if (button.click(0)) {
						ctx.onCondition(() -> !ctx.pathing.inArea(home), 3000);
					}
				}
			}
			if (ctx.portalTeleports.zenytePortalOpen() && main.buyMageArea == true) {
				ctx.sleep(1000);
				ctx.updateStatus("Teleporting to mage arena");
				SimpleWidget wilderness = ctx.widgets.getWidget(700, 4);
				SimpleWidget mageBank = ctx.widgets.getWidget(700, 11);
				ctx.mouse.click(new Point(135, 226));
				ctx.sleep(1000);
				if (wilderness != null) {
					SimpleWidget button = wilderness.getDynamicChildren()[12];
					if (button.click(0)) {
						ctx.onCondition(() -> !ctx.pathing.inArea(home), 3000);
					}
				}
				if (mageBank != null) {
					SimpleWidget button = mageBank.getDynamicChildren()[8];
					if (button.click(0)) {
						ctx.onCondition(() -> !ctx.pathing.inArea(home), 3000);
					}
				}
			}
		}
	}

	@Override
	public String status() {
		return "Checking if we got GP...";
	}

}
