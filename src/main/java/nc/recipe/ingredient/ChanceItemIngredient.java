package nc.recipe.ingredient;

import java.util.ArrayList;
import java.util.List;

import nc.config.NCConfig;
import nc.recipe.SorptionType;
import nc.util.NCMath;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class ChanceItemIngredient implements IItemIngredient {
	
	public IItemIngredient ingredient;
	public int chancePercent;
	public int minStackSize;
	
	public ChanceItemIngredient(IItemIngredient ingredient, int chancePercent) {
		this(ingredient, chancePercent, 0);
	}
	
	public ChanceItemIngredient(IItemIngredient ingredient, int chancePercent, int minStackSize) {
		this.ingredient = ingredient;
		this.chancePercent = MathHelper.clamp(chancePercent, 0, 100);
		this.minStackSize = MathHelper.clamp(minStackSize, 0, ingredient.getMaxStackSize());
	}

	@Override
	public ItemStack getStack() {
		return ingredient.getStack();
	}

	@Override
	public String getIngredientName() {
		return ingredient.getIngredientName() + " [ " + chancePercent + "%, min: " + minStackSize + " ]";
	}

	@Override
	public String getIngredientNamesConcat() {
		return ingredient.getIngredientNamesConcat() + " [ " + chancePercent + "%, min: " + minStackSize + " ]";
	}

	@Override
	public int getMaxStackSize() {
		return ingredient.getMaxStackSize();
	}
	
	@Override
	public void setMaxStackSize(int stackSize) {
		ingredient.setMaxStackSize(stackSize);
	}
	
	@Override
	public int getNextStackSize() {
		return minStackSize + NCMath.getBinomial(getMaxStackSize() - minStackSize, chancePercent);
	}

	@Override
	public List<ItemStack> getInputStackList() {
		List<ItemStack> stackList = new ArrayList<ItemStack>();
		for (ItemStack stack : ingredient.getInputStackList()) {
			for (int i = minStackSize; i <= getMaxStackSize(); i++) {
				if (i <= 0) {
					//stackList.add(null);
				} else {
					ItemStack newStack = stack.copy();
					newStack.setCount(i);
					stackList.add(newStack);
				}
			}
		}
		return stackList;
	}
	
	@Override
	public List<ItemStack> getOutputStackList() {
		List<ItemStack> stackList = new ArrayList<ItemStack>();
		for (int i = minStackSize; i <= getMaxStackSize(); i++) {
			if (i == 0) {
				if (NCConfig.jei_chance_items_include_null) stackList.add(null);
			} else {
				ItemStack newStack = getStack().copy();
				newStack.setCount(i);
				stackList.add(newStack);
			}
		}
		return stackList;
	}

	@Override
	public boolean matches(Object object, SorptionType sorption) {
		return ingredient.matches(object, sorption);
	}
}
