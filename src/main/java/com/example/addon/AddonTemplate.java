package com.example.addon;

import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Items;

public class AddonTemplate extends MeteorAddon {
    // Kategori ismini "Custom" olarak belirledik
    public static final Category CATEGORY = new Category("Custom");

    @Override
    public void onInitialize() {
        // Modülümüzü sisteme kaydediyoruz
        Modules.get().add(new ItemTracers());
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getPackage() {
        return "com.example.addon";
    }

    public static class ItemTracers extends Module {
        private final SettingGroup sgGeneral = settings.getDefaultGroup();

        // Çizgi rengi ayarı
        private final Setting<SettingColor> color = sgGeneral.add(new ColorSetting.Builder()
            .name("line-color")
            .description("İz çizgisinin rengi.")
            .defaultValue(new SettingColor(255, 0, 0, 255))
            .build()
        );

        public ItemTracers() {
            super(CATEGORY, "item-tracers", "Yerdeki Antik Kalıntıları (Ancient Debris) takip eder.");
        }

        @EventHandler
        private void onRender(Render3DEvent event) {
            // Dünya veya oyuncu yüklenmemişse işlem yapma
            if (mc.world == null || mc.player == null) return;

            // Dünyadaki tüm varlıkları tara
            for (Entity entity : mc.world.getEntities()) {
                // Eğer varlık bir yerdeki eşya (ItemEntity) ise
                if (entity instanceof ItemEntity item) {
                    // Sadece Antik Kalıntı ise devam et
                    if (item.getStack().getItem() == Items.ANCIENT_DEBRIS) {
                        
                        // Çizgiyi oyuncunun göz hizasından eşyanın konumuna çizer
                        // 1.21.4'te event.renderer.line kullanımı en stabil yöntemdir
                        event.renderer.line(
                            mc.player.getX(), 
                            mc.player.getEyeY(), 
                            mc.player.getZ(),
                            item.getX(), 
                            item.getY() + 0.1, // Çizgi eşyanın biraz üzerinde bitsin
                            item.getZ(),
                            color.get()
                        );
                    }
                }
            }
        }
    }
}
