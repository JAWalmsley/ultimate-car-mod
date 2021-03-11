package de.maxhenkel.car.blocks.tileentity;

import de.maxhenkel.car.Main;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Arrays;

public class TileEntitySign extends TileEntityBase {

    private String[] text = new String[8];

    public TileEntitySign() {
        super(Main.SIGN_TILE_ENTITY_TYPE);
        Arrays.fill(text, "");
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        for (int i = 0; i < text.length; i++) {
            compound.putString("text" + i, text[i]);
        }
        return super.save(compound);
    }

    @Override
    public void load(BlockState blockState, CompoundNBT compound) {
        for (int i = 0; i < text.length; i++) {
            this.text[i] = compound.getString("text" + i);
        }
        super.load(blockState, compound);
    }

    public String getText(int i) {
        if (i < 0 || i >= text.length) {
            return "";
        }
        return text[i];
    }

    public String[] getSignText() {
        return text;
    }

    public void setText(int i, String s) {
        if (i < 0 || i >= text.length || s == null) {
            return;
        }
        text[i] = s;
        setChanged();
        synchronize();
    }

    public void setText(String[] s) {
        if (s == null || s.length != text.length) {
            return;
        }
        text = s;
        setChanged();
        synchronize();
    }

    @Override
    public ITextComponent getTranslatedName() {
        return new TranslationTextComponent("block.car.sign");
    }

    @Override
    public IIntArray getFields() {
        return new IntArray(0);
    }
}
