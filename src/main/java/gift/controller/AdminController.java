package gift.controller;

import gift.dto.ProductRequestDto;
import gift.dto.ProductResponseDto;
import gift.dto.UpdateProductRequestDto;
import gift.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

  private final ProductService productService;

  public AdminController(ProductService productService) {
    this.productService = productService;
  }

  @GetMapping
  public String productList(Model model, @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
    Pageable fixedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
    Page<ProductResponseDto> productPage = productService.findAllProduct(fixedPageable);
    model.addAttribute("productPage", productPage);
    return "admin/list"; // templates/admin/list.html
  }

  @GetMapping("/add")
  public String addForm(Model model) {
    model.addAttribute("product", ProductRequestDto.EMPTY);
    model.addAttribute("mode", "add");
    return "admin/form";
  }

  @PostMapping("/add")
  public String addProduct(@Valid @ModelAttribute("product") ProductRequestDto productRequestDto,
      BindingResult bindingResult,
      Model model) {
    if (bindingResult.hasErrors()) {
      model.addAttribute("mode", "add");
      return "admin/form";
    }
    productService.addProduct(productRequestDto);
    return "redirect:/admin";
  }

  @GetMapping("/edit/{id}")
  public String editForm(@PathVariable Long id, Model model) {
    ProductResponseDto product = productService.findProductById(id);
    UpdateProductRequestDto productRequestDto = new UpdateProductRequestDto(
        id,
        product.name(),
        product.price(),
        product.imageUrl(),
        false
    );
    model.addAttribute("product", productRequestDto);
    model.addAttribute("productId", id);
    model.addAttribute("mode", "edit");
    return "admin/form";
  }

  @PostMapping("/edit/{id}")
  public String editProduct(@PathVariable Long id,
      @Valid @ModelAttribute("product") UpdateProductRequestDto productRequestDto,
      BindingResult bindingResult,
      Model model) {
    if (bindingResult.hasErrors()) {
      model.addAttribute("mode", "edit");
      model.addAttribute("productId", id);
      return "admin/form";
    }
    productService.updateProduct(productRequestDto);
    return "redirect:/admin";
  }

  @PostMapping("/delete/{id}")
  public String deleteProduct(@PathVariable Long id) {
    productService.deleteProduct(id);
    return "redirect:/admin";
  }
}
