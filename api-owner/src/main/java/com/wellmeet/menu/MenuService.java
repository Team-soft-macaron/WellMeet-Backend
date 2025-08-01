package com.wellmeet.menu;

import com.wellmeet.auth.dto.MessageResponse;
import com.wellmeet.domain.owner.OwnerDomainService;
import com.wellmeet.domain.owner.entity.Owner;
import com.wellmeet.domain.restaurant.RestaurantDomainService;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import com.wellmeet.domain.restaurant.menu.entity.Menu;
import com.wellmeet.domain.restaurant.menu.repository.MenuRepository;
import com.wellmeet.exception.ErrorCode;
import com.wellmeet.exception.WellMeetException;
import com.wellmeet.menu.dto.CreateMenuRequest;
import com.wellmeet.menu.dto.MenuListResponse;
import com.wellmeet.menu.dto.MenuResponse;
import com.wellmeet.menu.dto.UpdateMenuAvailabilityRequest;
import com.wellmeet.menu.dto.UpdateMenuRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final OwnerDomainService ownerDomainService;
    private final RestaurantDomainService restaurantDomainService;

    @Transactional(readOnly = true)
    public MenuListResponse getMenus(Long ownerId) {
        Owner owner = ownerDomainService.getById(ownerId);
        Restaurant restaurant = owner.getRestaurant();

        List<Menu> menus = menuRepository.findByRestaurantId(restaurant.getId());
        List<MenuResponse> menuResponses = menus.stream()
                .map(MenuResponse::new)
                .toList();

        return new MenuListResponse(menuResponses);
    }

    @Transactional
    public MenuResponse createMenu(Long ownerId, CreateMenuRequest request) {
        Owner owner = ownerDomainService.getById(ownerId);
        Restaurant restaurant = owner.getRestaurant();

        Menu menu = new Menu(
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                restaurant
        );

        Menu savedMenu = menuRepository.save(menu);
        return new MenuResponse(savedMenu);
    }

    @Transactional
    public MenuResponse updateMenu(Long ownerId, Long menuId, UpdateMenuRequest request) {
        Owner owner = ownerDomainService.getById(ownerId);
        Menu menu = getMenuByIdAndOwner(menuId, owner);

        // TODO: Menu 엔티티에 update 메서드 추가 필요
        // menu.update(request.getName(), request.getDescription(), request.getPrice());

        Menu savedMenu = menuRepository.save(menu);
        return new MenuResponse(savedMenu);
    }

    @Transactional
    public void deleteMenu(Long ownerId, Long menuId) {
        Owner owner = ownerDomainService.getById(ownerId);
        Menu menu = getMenuByIdAndOwner(menuId, owner);

        menuRepository.delete(menu);
    }

    @Transactional
    public MenuResponse updateMenuAvailability(Long ownerId, Long menuId,
                                               UpdateMenuAvailabilityRequest request) {
        Owner owner = ownerDomainService.getById(ownerId);
        Menu menu = getMenuByIdAndOwner(menuId, owner);

        // TODO: Menu 엔티티에 available 필드와 updateAvailability 메서드 추가 필요
        // menu.updateAvailability(request.getAvailable());

        Menu savedMenu = menuRepository.save(menu);
        return new MenuResponse(savedMenu);
    }

    private Menu getMenuByIdAndOwner(Long menuId, Owner owner) {
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new WellMeetException(ErrorCode.MENU_NOT_FOUND));

        // 해당 사장님의 레스토랑 메뉴인지 확인
        if (!menu.getRestaurant().getId().equals(owner.getRestaurant().getId())) {
            throw new WellMeetException(ErrorCode.FORBIDDEN);
        }

        return menu;
    }
}
