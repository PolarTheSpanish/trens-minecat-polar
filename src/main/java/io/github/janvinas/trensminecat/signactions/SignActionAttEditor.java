package io.github.janvinas.trensminecat.signactions;

import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.tc.attachments.api.Attachment;
import com.bergerkiller.bukkit.tc.attachments.api.AttachmentTypeRegistry;
import com.bergerkiller.bukkit.tc.attachments.config.AttachmentModel;
import com.bergerkiller.bukkit.tc.attachments.control.CartAttachmentText;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.properties.CartProperties;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;
import java.util.List;

public class SignActionAttEditor extends SignAction {
    @Override
    public boolean match(SignActionEvent info) {
        return info.isType("atteditor");
    }

    @Override
    public boolean canSupportRC() {
        return false;
    }

    @Override
    public boolean build(SignChangeActionEvent event) {
        if (!event.isType("atteditor")) {
            return false;
        }

        return SignBuildOptions.create()
                .setName("atteditor")
                .handle(event.getPlayer());
    }

    @Override
    public void execute(SignActionEvent info) {
        if (info.isTrainSign() && info.isAction(SignActionType.GROUP_ENTER, SignActionType.REDSTONE_ON)) {
            if (!info.isPowered()) return;
            String text = info.getLine(2) + info.getLine(3);
            if (text.isEmpty() || text.isBlank()) text = " ";
            MinecartMember<?> member = info.getMember();
            List<Attachment> attachments = member.getAttachments().getRootAttachment().getChildren();
            for (Attachment attachment : attachments) {
                ConfigurationNode config = attachment.getConfig();
                if (AttachmentTypeRegistry.instance().fromConfig(config) == CartAttachmentText.TYPE) {
                    config.set("posY", "0.75");
                    config.set("text", text);
                    member.getProperties().getModel().updateNode(attachment.getPath(), config);
                }
            }
        }
    }
}