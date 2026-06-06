package net.zincstudios.scgextra.entity.cog.centipede;

import net.zincstudios.scgextra.entity.asgharian.WeakPointPart;

// Because headshot box doesn't work well on subentities
public class CogCentipedeWeakpointPartEntity extends CogCentipedeSegmentPartEntity implements WeakPointPart {

    public CogCentipedeWeakpointPartEntity(CogCentipedeEntity parent, float width, float height) {
        super(parent, width, height);
    }
}
