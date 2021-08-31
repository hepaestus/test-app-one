import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { DriverService } from '../service/driver.service';

import { DriverComponent } from './driver.component';

describe('Component Tests', () => {
  describe('Driver Management Component', () => {
    let comp: DriverComponent;
    let fixture: ComponentFixture<DriverComponent>;
    let service: DriverService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [DriverComponent],
      })
        .overrideTemplate(DriverComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(DriverComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(DriverService);

      const headers = new HttpHeaders().append('link', 'link;link');
      jest.spyOn(service, 'query').mockReturnValue(
        of(
          new HttpResponse({
            body: [{ id: 123 }],
            headers,
          })
        )
      );
    });

    it('Should call load all on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.drivers?.[0]).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
