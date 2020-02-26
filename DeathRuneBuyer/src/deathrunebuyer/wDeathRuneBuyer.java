package deathrunebuyer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;

import deathrunebuyer.banktask.BankTask;
import deathrunebuyer.buytasks.BuyIsle;
import deathrunebuyer.buytasks.BuyMageArea;
import deathrunebuyer.walktask.WalkIsleShop;
import simple.hooks.scripts.Category;
import simple.hooks.scripts.ScriptManifest;
import simple.hooks.scripts.task.Task;
import simple.hooks.scripts.task.TaskScript;
import simple.hooks.simplebot.ChatMessage;

@ScriptManifest(author = "wrrion15", category = Category.OTHER, description = "<br>Buys death runes from Baba Yaga at lunar isle and Lundail inside mage arena bank! Start script at home.",
discord = "Daniel#0157", name = "|w|DeathRuneBuyer", servers = { "Zenyte" }, version = "0.1")
public class wDeathRuneBuyer extends TaskScript {

	public boolean buyIsle = false;
	public boolean buyMageArea = false;

	private long startTime = 0;
	public int prevInvCount;
	public int runesBought;
	public int amountCoins;

	private List<Task> tasks = new ArrayList<Task>();

	@Override
	public void paint(Graphics g) {
		long runTime = System.currentTimeMillis() - startTime;

		Color color1 = new Color(0, 0, 0);
		Color color2 = new Color(0, 102, 51);
		Color color3 = new Color(255, 0, 0);

		Font font1 = new Font("Arial", 0, 12);

		g.setColor(color1);
		g.fillRect(5, 4, 190, 70);
		g.setColor(color2);
		g.drawRect(5, 4, 190, 70);
		g.setFont(font1);
		g.setColor(color3);
		g.drawString("Runtime: "+ formatTime(runTime), 12, 23);
		g.drawString("Bought: "+runesBought+" death runes", 12, 42);
		g.drawString("|w|DeathRuneBuyer by Daniel", 12, 62);
	}

	@Override
	public boolean prioritizeTasks() {
		return true;
	}

	@Override
	public List<Task> tasks() {
		return tasks;
	}

	@Override
	public void onChatMessage(ChatMessage m) {

	}

	@Override
	public void onExecute() {
		int min = 45000;
		String value = JOptionPane.showInputDialog(null,
                "45k is minimum",
                "Enter amount of coins to use",
                JOptionPane.INFORMATION_MESSAGE);
		try {
			int total= Integer.parseInt(value);
			if (total < min) total = min;
			System.out.println("Amount of coins to use: "+total);
			amountCoins = total;
		} catch(NumberFormatException e) {
			ctx.updateStatus("Enter only numeric digits!");
			ctx.stopScript();
		}
		prevInvCount = ctx.inventory.populate().filter("Death rune").population(true);
		buyIsle = true;
		buyMageArea = false;
		tasks.addAll(Arrays.asList(new BankTask(ctx, this), new WalkIsleShop(ctx), new BuyIsle(ctx, this), new BuyMageArea(ctx, this)));
	}

	@Override
	public void onTerminate() {

	}

	private String formatTime(final long ms) {
		long s = ms / 1000, m = s / 60, h = m / 60;
		s %= 60;
		m %= 60;
		h %= 24;
		return String.format("%02d:%02d:%02d", h, m, s);
	}

}
