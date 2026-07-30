package net.tuna.member.validation;

import jakarta.validation.GroupSequence;
import jakarta.validation.groups.Default;

@GroupSequence({
        Default.class,
        ValidationGroups.NotBlankGroup.class,
        ValidationGroups.EmailGroup.class,
        ValidationGroups.PatternGroup.class,
        ValidationGroups.SizeGroup.class
})
public interface ValidationSequence {
}
