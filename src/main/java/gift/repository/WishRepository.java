package gift.repository;

import gift.entity.Member;
import gift.entity.Product;
import gift.entity.Wish;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WishRepository extends JpaRepository<Wish, Long> {
    Page<Wish> findByMember(Member member, Pageable pageable);

    boolean existsByMemberAndProduct(Member member, Product product);

    void deleteByMemberAndProduct(Member member, Product product);

    Optional<Wish> findByMemberAndProduct(Member member, Product product);
}
