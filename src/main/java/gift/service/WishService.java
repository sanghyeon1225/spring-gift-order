package gift.service;

import gift.dto.WishRequestDto;
import gift.dto.WishResponseDto;
import gift.entity.Member;
import gift.entity.Product;
import gift.entity.Wish;
import gift.exception.DuplicateWishException;
import gift.exception.ResourceNotFoundException;
import gift.repository.MemberRepository;
import gift.repository.ProductRepository;
import gift.repository.WishRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WishService {
    private final WishRepository wishRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;

    public WishService(
            WishRepository wishRepository,
            ProductRepository productRepository,
            MemberRepository memberRepository
    ) {
        this.wishRepository = wishRepository;
        this.productRepository = productRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional(readOnly = true)
    public Page<WishResponseDto> getWishes(Long memberId,  Pageable pageable) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 회원을 찾을 수 없습니다. ID: " + memberId));

        return wishRepository.findByMember(member, pageable)
                .map(wish -> {
                    Product product = wish.getProduct();
                    return new WishResponseDto(
                            wish.getId(),
                            product.getId(),
                            product.getName(),
                            product.getPrice(),
                            product.getImageUrl()
                    );
                });
    }

    @Transactional
    public void addWish(Long memberId, WishRequestDto wishRequestDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 회원을 찾을 수 없습니다. ID: " + memberId));
        Product product = productRepository.findById(wishRequestDto.productId())
                .orElseThrow(() -> new ResourceNotFoundException("해당 상품을 찾을 수 없습니다. ID: " + wishRequestDto.productId()));

        if (wishRepository.existsByMemberAndProduct(member, product)) {
            throw new DuplicateWishException("이미 위시리스트에 추가된 상품입니다.");
        }

        Wish wish = new Wish(member, product);
        wishRepository.save(wish);
    }

    @Transactional
    public void deleteWish(Long memberId, Long productId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 회원을 찾을 수 없습니다. ID: " + memberId));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 상품을 찾을 수 없습니다. ID: " + productId));

        if (!wishRepository.existsByMemberAndProduct(member, product)) {
            throw new ResourceNotFoundException("해당 상품이 위시리스트에 존재하지 않습니다.");
        }

        wishRepository.deleteByMemberAndProduct(member, product);
    }
}
