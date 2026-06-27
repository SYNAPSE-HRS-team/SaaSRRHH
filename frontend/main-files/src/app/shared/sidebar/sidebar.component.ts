import { Component, OnInit } from '@angular/core';
import { ROUTES } from './sidebar-routes.config';
import { Router, Event, NavigationStart, NavigationEnd, NavigationError } from '@angular/router';
import { SidebarService } from './sidebar.service';
import { AuthService } from '../../auth/auth.service';

import * as $ from 'jquery';

@Component({
    selector: 'app-sidebar',
    templateUrl: './sidebar.component.html',
})
export class SidebarComponent implements OnInit {
    public menuItems: any[];

    constructor(
        public sidebarservice: SidebarService,
        private router: Router,
        private authService: AuthService,
    ) {
        router.events.subscribe((event: Event) => {
            if (event instanceof NavigationStart) {}
            if (event instanceof NavigationEnd && $(window).width() < 1025 && (document.readyState == 'complete' || false)) {
                this.toggleSidebar();
            }
            if (event instanceof NavigationError) {
                console.log(event.error);
            }
        });
    }

    toggleSidebar() {
        this.sidebarservice.setSidebarState(!this.sidebarservice.getSidebarState());
        if ($('.wrapper').hasClass('nav-collapsed')) {
            $('.wrapper').removeClass('nav-collapsed');
            $('.sidebar-wrapper').unbind('hover');
        } else {
            $('.wrapper').addClass('nav-collapsed');
            $('.sidebar-wrapper').hover(
                function () { $('.wrapper').addClass('sidebar-hovered'); },
                function () { $('.wrapper').removeClass('sidebar-hovered'); },
            );
        }
    }

    getSideBarState() { return this.sidebarservice.getSidebarState(); }

    hideSidebar() { this.sidebarservice.setSidebarState(true); }

    ngOnInit() {
        this.menuItems = this.filtrarMenuPorRol(ROUTES.filter(menuItem => menuItem));
        $.getScript('./assets/js/app-sidebar.js');
    }

    private filtrarMenuPorRol(items: any[]): any[] {
        if (this.authService.isEmpleado() && !this.authService.isAdmin() && !this.authService.isSupervisor()) {
            return [
                { path: '/dashboard/mi-asistencia', title: 'Mi asistencia', icon: 'bx bx-qr-scan', class: '', badge: '', badgeClass: '', isExternalLink: false, submenu: [] },
                { path: '/user-profile', title: 'Mi perfil', icon: 'bx bx-user-circle', class: '', badge: '', badgeClass: '', isExternalLink: false, submenu: [] },
            ];
        }
        if (this.authService.isSupervisor() && !this.authService.isAdmin()) {
            return [
                { path: '/dashboard/human-resources', title: 'Asistencias', icon: 'bx bx-time-five', class: '', badge: '', badgeClass: '', isExternalLink: false, submenu: [] },
                { path: '/user-profile', title: 'Mi perfil', icon: 'bx bx-user-circle', class: '', badge: '', badgeClass: '', isExternalLink: false, submenu: [] },
            ];
        }
        return [
            { path: '/dashboard/human-resources', title: 'Asistencias', icon: 'bx bx-time-five', class: '', badge: '', badgeClass: '', isExternalLink: false, submenu: [] },
            { path: '/usuarios', title: 'Usuarios', icon: 'bx bx-user', class: '', badge: '', badgeClass: '', isExternalLink: false, submenu: [] },
            { path: '/user-profile', title: 'Mi perfil', icon: 'bx bx-user-circle', class: '', badge: '', badgeClass: '', isExternalLink: false, submenu: [] },
        ];
    }
}
