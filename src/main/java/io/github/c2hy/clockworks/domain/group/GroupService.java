package io.github.c2hy.clockworks.domain.group;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;

    public Group createOrUpdateGroup(GroupDTO groupDTO) {
        if (groupDTO == null) {
            return Group.createNullGroup();
        }

        var group = groupRepository.findById(groupDTO.getId())
                .map(v -> v.merge(groupDTO))
                .orElseGet(() -> Group.create(groupDTO));

        group.check();

        return group;
    }

    public void save(Group group) {
        if (group.ignoreUpdate()) {
            return;
        }

        groupRepository.save(group);
    }
}
