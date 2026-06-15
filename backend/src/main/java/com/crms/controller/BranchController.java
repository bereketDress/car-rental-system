package com.crms.controller;
import lombok.RequiredArgsConstructor;
import com.crms.model.Branch;
import com.crms.service.BranchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/branches")
@RequiredArgsConstructor
public class BranchController {

    private final BranchService branchService;

    @GetMapping
    public ResponseEntity<List<Branch>> listAll() {
        return ResponseEntity.ok(branchService.listBranches());
    }

    @PostMapping
    public ResponseEntity<Branch> add(@RequestBody Branch branch) {
        return ResponseEntity.ok(branchService.addBranch(branch));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Branch> update(@PathVariable Long id, @RequestBody Branch branch) {
        return ResponseEntity.ok(branchService.updateBranch(id, branch));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        branchService.deleteBranch(id);
        return ResponseEntity.noContent().build();
    }
}
