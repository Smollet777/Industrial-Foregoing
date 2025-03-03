/*
 * This file is part of Industrial Foregoing.
 *
 * Copyright 2021, Buuz135
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in the
 * Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.buuz135.industrial.api.transporter;

import com.buuz135.industrial.IndustrialForegoing;
import com.buuz135.industrial.api.IBlockContainer;
import com.buuz135.industrial.api.conveyor.gui.IGuiComponent;
import com.buuz135.industrial.gui.component.StateButtonInfo;
import com.buuz135.industrial.gui.component.custom.RegulatorFilterGuiComponent;
import com.buuz135.industrial.gui.component.custom.TexturedStateButtonGuiComponent;
import com.buuz135.industrial.proxy.block.filter.RegulatorFilter;
import com.buuz135.industrial.proxy.network.TransporterSyncMessage;
import com.buuz135.industrial.utils.Reference;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public abstract class FilteredTransporterType<TYPE, CAP> extends TransporterType {

    private RegulatorFilter<TYPE, CAP> filter;
    private boolean whitelist;
    private boolean isRegulated;

    public FilteredTransporterType(IBlockContainer container, TransporterTypeFactory factory, Direction side, TransporterTypeFactory.TransporterAction action) {
        super(container, factory, side, action);
        this.filter = createFilter();
        this.whitelist = false;
        this.isRegulated = false;
    }

    public boolean hasGui() {
        return true;
    }

    public void handleButtonInteraction(int buttonId, CompoundNBT compound) {
        if (buttonId >= 0 && buttonId < filter.getFilter().length) {
            if (compound.contains("Amount")) {
                this.filter.getFilter()[buttonId].increaseAmount(compound.getInt("Amount"));
            } else {
                this.filter.setFilter(buttonId, ItemStack.read(compound));
            }
            this.getContainer().requestSync();
        }
        if (buttonId == 16) {
            whitelist = !whitelist;
            this.getContainer().requestSync();
        }
        if (buttonId == 17) {
            isRegulated = !isRegulated;
            this.getContainer().requestSync();
        }
    }

    public abstract RegulatorFilter<TYPE, CAP> createFilter();

    public void handleRenderSync(Direction origin, CompoundNBT compoundNBT) {

    }

    public void syncRender(Direction origin, CompoundNBT compoundNBT) {
        IndustrialForegoing.NETWORK.sendToNearby(getWorld(), getPos(), 32, new TransporterSyncMessage(getPos(), compoundNBT, getSide().getIndex(), origin.getIndex()));
    }

    public void addComponentsToGui(List<IGuiComponent> componentList) {
        super.addComponentsToGui(componentList);
        componentList.add(new RegulatorFilterGuiComponent(this.filter.getLocX(), this.filter.getLocY(), this.filter.getSizeX(), this.filter.getSizeY()) {
            @Override
            public RegulatorFilter<TYPE, CAP> getFilter() {
                return FilteredTransporterType.this.filter;
            }

            @Override
            public boolean isRegulator() {
                return isRegulated;
            }
        });
        ResourceLocation res = new ResourceLocation(Reference.MOD_ID, "textures/gui/machines.png");
        componentList.add(new TexturedStateButtonGuiComponent(16, 133, 20, 18, 18,
                new StateButtonInfo(0, res, 1, 214, new String[]{"whitelist"}),
                new StateButtonInfo(1, res, 20, 214, new String[]{"blacklist"})) {
            @Override
            public int getState() {
                return whitelist ? 0 : 1;
            }
        });
        componentList.add(new TexturedStateButtonGuiComponent(17, 133, 40, 18, 18,
                new StateButtonInfo(0, res, 58, 233, new String[]{"regulated_true"}),
                new StateButtonInfo(1, res, 58 + 19, 233, new String[]{"regulated_false"})) {
            @Override
            public int getState() {
                return isRegulated ? 0 : 1;
            }
        });
    }

    public boolean ignoresCollision() {
        return false;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT compoundNBT = super.serializeNBT();
        compoundNBT.put("Filter", filter.serializeNBT());
        compoundNBT.putBoolean("Whitelist", whitelist);
        compoundNBT.putBoolean("Regulated", isRegulated);
        return compoundNBT;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);
        if (nbt.contains("Filter")) filter.deserializeNBT(nbt.getCompound("Filter"));
        whitelist = nbt.getBoolean("Whitelist");
        isRegulated = nbt.getBoolean("Regulated");
    }

    public RegulatorFilter<TYPE, CAP> getFilter() {
        return filter;
    }

    public boolean isWhitelist() {
        return whitelist;
    }

    public boolean isRegulated() {
        return isRegulated;
    }
}
