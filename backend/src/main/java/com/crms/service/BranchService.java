package com.crms.service;
import com.crms.model.*;
import com.crms.repository.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BranchService {

    private final BranchRepository branchRepository;

    public List<Branch> listBranches() {
        return branchRepository.findAll(); }

    public Branch getById(Long id) {
        return branchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Branch not found: " + id));
    }

    public Branch addBranch(Branch branch) {
        return branchRepository.save(branch); }

    public Branch updateBranch(Long id, Branch branch) {
        Branch existing = getById(id);
        existing.setName(branch.getName());
        existing.setPhone(branch.getPhone());
        existing.setAddress(branch.getAddress());
        return branchRepository.save(existing);
    }

    public boolean deleteBranch(Long id) {
        branchRepository.deleteById(id);
        return true;
    }
}
