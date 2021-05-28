package eu.octanne.edora.client.mixin;

import com.mojang.blaze3d.systems.RenderSystem;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import eu.octanne.edora.EdoraMain;
import eu.octanne.edora.client.screen.menu.EdoraInventoryScreen;
import eu.octanne.edora.packet.MenuType;
import eu.octanne.edora.packet.client.PacketClients;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

@Pseudo
@Mixin(InventoryScreen.class)
public class MixinInventoryScreen extends AbstractInventoryScreen<PlayerScreenHandler> implements RecipeBookProvider, EdoraInventoryScreen {

    @Unique
    private static final Identifier EDORA_MAIN_MENU = new Identifier(EdoraMain.MOD_ID,"textures/gui/container/inv_main_menu.png");
    @Shadow
    private static final Identifier RECIPE_BUTTON_TEXTURE = new Identifier("textures/gui/recipe_button.png");
    @Shadow
    private float mouseX;
    @Shadow
    private float mouseY;
    @Shadow
    private final RecipeBookWidget recipeBook = new RecipeBookWidget();

    @Unique
    private boolean edoraMenuOpenState = false;

    @Shadow
    private boolean open;
    @Shadow
    private boolean narrow;
    @Shadow
    private boolean mouseDown;

    public MixinInventoryScreen(PlayerEntity player) {
        super(player.playerScreenHandler, player.inventory, new TranslatableText("container.crafting"));
        this.passEvents = true;
        this.titleX = 97;
    }

    @Inject(at = @At("RETURN"), method = "init()V")
    private void init(CallbackInfo info) {
        // Move RECIPE
        moveRecipeBook();
        // ADD Menu Button
        TexturedButtonWidget menuButton = new TexturedButtonWidget(this.x + 100, this.y + 61, 20, 18, 223, 0, 18, EDORA_MAIN_MENU, 256, 256,
        buttonWidget -> {
            if(!edoraMenuOpenState) PacketClients.pcktClientAskOpenMenu.send(MenuType.PERSONAL_MENU);
            else toggleEdoraMenu();
            this.mouseDown = true;
        });
        this.addButton(menuButton);
        // ADD Other Menu Button
        this.addButton(new TexturedButtonWidget(this.x + 146, this.y + 61, 20, 18, 223, 36, 18, EDORA_MAIN_MENU, 256, 256,
        buttonWidget -> {
            this.mouseDown = true;
        }));
    }

    private void moveRecipeBook() {
        if(!this.buttons.isEmpty()){
            this.buttons.get(0).active = false;
            this.buttons.remove(0);
            this.addButton(new TexturedButtonWidget(this.x + 123, this.y + 61, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEXTURE,
            buttonWidget -> {
                this.recipeBook.reset(this.narrow);
                this.recipeBook.toggleOpen();
                int lastX = this.x;
                this.x = this.recipeBook.findLeftEdge(this.narrow, this.width, this.backgroundWidth);
                this.mouseDown = true;
                for (AbstractButtonWidget button : this.buttons) {
                    button.x = button.x-lastX+this.x;
                }
            }));
        }
    }

    @Override
    public void refreshRecipeBook() {
        this.recipeBook.refresh();
    }

    @Override
    public RecipeBookWidget getRecipeBookWidget() {
        return this.recipeBook;
    }

    @Override
    public void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.client.getTextureManager().bindTexture(edoraMenuOpenState ? EDORA_MAIN_MENU : BACKGROUND_TEXTURE);
        int i = this.x;
        int j = this.y;


        this.drawTexture(matrices, edoraMenuOpenState ? i-47 : i, j, 0, 0, edoraMenuOpenState ? 223 : this.backgroundWidth, this.backgroundHeight);
        // DRAW SLOT OF SECOND ARMOR
        this.client.getTextureManager().bindTexture(EDORA_MAIN_MENU);
        this.drawTexture(matrices, x + 76, y + 7, 123, 7, 18, 54);
        InventoryScreen.drawEntity(i + 51, j + 75, 30, (float)(i + 51) - this.mouseX, (float)(j + 75 - 50) - this.mouseY, this.client.player);
    }

    @Override
    public void openEdoraMenu(CompoundTag dataTAG) {
        edoraMenuOpenState = true;
        // TODO read DATA to SHOW
    }

    @Override
    public void closeEdoraMenu() {
        edoraMenuOpenState = false;
    }

    @Override
    public boolean toggleEdoraMenu() {
        edoraMenuOpenState = !edoraMenuOpenState;
        return edoraMenuOpenState;
    }

    @Override
    public void updateData(CompoundTag dataTAG) {
        // TODO read DATA to UPDATE
        
    }
}